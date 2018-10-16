package cn.fintecher.pangolin.service.dataimp.service;

import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.entity.domain.*;
import cn.fintecher.pangolin.service.dataimp.repository.BaseCaseImportExcelTempRepository;
import cn.fintecher.pangolin.service.dataimp.repository.BasePersonalImportExcelTempRepository;
import cn.fintecher.pangolin.service.dataimp.repository.MergeDataModelRepository;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.fieldMaskingSpanQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 19:59 2018/9/4
 */
@Service("mergeDataService")
public class MergeDataService {

    Logger logger= LoggerFactory.getLogger(MergeDataService.class);

    @Autowired
    BaseCaseImportExcelTempRepository baseCaseImportExcelTempRepository;

    @Autowired
    MergeDataModelRepository mergeDataModelRepository;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 币种数据合并规则
     * 光大银行：存在一卡多币种的情况，其中102/103是人民币,其他的为美元。合并规则：通过币种进行数据转换
     * @param importDataExcelRecord
     */

    public void currencyMergeData(ImportDataExcelRecord importDataExcelRecord) throws BadRequestException {
        logger.info("{} 批次开始数据币种合并........", importDataExcelRecord.getBatchNumber());
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must().add(matchPhraseQuery("batchNumber.keyword", importDataExcelRecord.getBatchNumber()));
        Iterable<BaseCaseAllImportExcelTemp> baseCaseIterables = baseCaseImportExcelTempRepository.search(queryBuilder);
        if (baseCaseIterables.iterator().hasNext()) {
            List<BaseCaseAllImportExcelTemp> baseCaseList = Lists.newArrayList(baseCaseIterables);
            //数据合并结果
            List<BaseCaseAllImportExcelTemp> updateList = new ArrayList<>();
            if (importDataExcelRecord.getPrincipalName().contains("光大银行")) {
                //101是美元,其他为人民币
                for (BaseCaseAllImportExcelTemp baseObj : baseCaseList) {
                    if ("101".equals(baseObj.getCurrency())) {
                        baseObj.setOverdueAmtTotal(0.0);//委案金额(人民币)
                        baseObj.setOverdueAmtTotalDollar(baseObj.getOverdueAmtTotal());//委案金额(美元)
                        baseObj.setCapitalAmt(0.0);//本金(人民币)
                        baseObj.setCapitalAmtDollar(baseObj.getCapitalAmt());//本金(美元)
                        baseObj.setLeftAmt(0.0);//欠款(人民币)
                        baseObj.setLeftAmtDollar(baseObj.getLeftAmt());//欠款(美元)
                        baseObj.setInterestAmt(0.0);//利息(人民币)
                        baseObj.setInterestAmtDollar(baseObj.getInterestAmt());//利息(美元)
                        baseObj.setLateFee(0.0);//滞纳金(人民币)
                        baseObj.setLateFeeDollar(baseObj.getLateFee());//滞纳金(美元)
                        baseObj.setServiceFee(0.0);//服务费(人民币)
                        baseObj.setServiceFeeDollar(baseObj.getServiceFee());//服务费(美元)
                        baseObj.setOverLimitFee(0.0);//超限费(人民币)
                        baseObj.setOverLimitFeeDollar(baseObj.getOverLimitFee());//超限费(美元)
                        baseObj.setFineFee(0.0);//违约金(人民币)
                        baseObj.setFineFeeDollar(baseObj.getFineFee());//违约金(美元)
                        updateList.add(baseObj);
                    }
                }
                if(!updateList.isEmpty()){
                    List<List<BaseCaseAllImportExcelTemp>> parts = Lists.partition(updateList, 1000);
                    parts.stream().forEach(list ->  baseCaseImportExcelTempRepository.saveAll(updateList));
                }
            }
            logger.info("{} 批次完成数据币种合并........", importDataExcelRecord.getBatchNumber());
        }
    }


    /**
     * 数据规则合并
     *  * 浦发银行：accountId为主键，第一条数据为汇总数据，紧跟着为卡信息数据，最后一条数据为汇总数据包含最低还款金额，但是无accoutID
     *          合并规则:读取数据是记录第一条的accoutID,当accoutnID为空时认为数据集结束，开始汇总数据
     *          联系信息也需要后合并：type为联系属性：本人/联系人/亲属;
     *          地址类型：RELATION:公司/家里/手机/其它/户籍
     * @param importDataExcelRecord
     * @throws BadRequestException
     */
    public void mergeData(ImportDataExcelRecord importDataExcelRecord) throws BadRequestException{
        logger.info("{} 批次开始卡信息合并........", importDataExcelRecord.getBatchNumber());
        BoolQueryBuilder queryBuilder= QueryBuilders.boolQuery();
        queryBuilder.must().add(matchPhraseQuery("batchNumber.keyword",importDataExcelRecord.getBatchNumber()));
        Iterable<BaseCaseAllImportExcelTemp> baseCaseIterables=baseCaseImportExcelTempRepository.search(queryBuilder);
        if(baseCaseIterables.iterator().hasNext()) {
            List<BaseCaseAllImportExcelTemp> baseCaseList = Lists.newArrayList(baseCaseIterables);
            //数据记录
            Map<String, BaseCaseAllImportExcelTemp> resultMap = new HashMap<>();
            //数据合并结果
            List<BaseCaseAllImportExcelTemp> updateList = new ArrayList<>();
            //删除已经合并的数据
            Set<String> ids = new HashSet<>();
            //记录合并的数据
            List<MergeDataModel> mergeDataModels = new ArrayList<>();
            if(importDataExcelRecord.getPrincipalName().contains("浦发银行")){
                //案件信息合并
                List<BaseCaseAllImportExcelTemp> tmpList=new ArrayList<>();
                //数据排序
                baseCaseList.sort(Comparator.comparing(BaseCaseAllImportExcelTemp::getSequenceNo));
                for(BaseCaseAllImportExcelTemp baseObj:baseCaseList) {
                    if(StringUtils.isBlank(baseObj.getCaseNumber())){
                        //卡信息
                        Map<String,CardInformation> cardInformationMap=new HashMap<>();
                        //逾期阶段合并
                        Set<String> handsNumberSet=new HashSet<>();
                        //数据集合结束开始数据合并
                        BaseCaseAllImportExcelTemp tmp=tmpList.get(0);
                        baseObj.setCaseNumber(tmp.getCaseNumber());
                        baseObj.setPrimaryKey(tmp.getPrimaryKey());
                        //卡信息
                        createCardInfo(baseObj, cardInformationMap);
                        if(StringUtils.isBlank(baseObj.getHandsNumber())){
                            handsNumberSet.add(baseObj.getHandsNumber());
                        }
                        baseObj.setHandsNumberSet(handsNumberSet);

                        tmpList.forEach(firstObj->{
                                //合并卡信息
                            createCardInfo(firstObj, cardInformationMap);
                            if(StringUtils.isBlank(firstObj.getHandsNumber())){
                                    handsNumberSet.add(firstObj.getHandsNumber());
                            }
                            createMergeDataModel(importDataExcelRecord, mergeDataModels, firstObj);
                            ids.add(firstObj.getId());
                        });
                        baseObj.setCardInformationSet(cardInformationMap.values().stream().collect(Collectors.toSet()));
                        updateList.add(baseObj);
                        tmpList.clear();
                    }else {
                        tmpList.add(baseObj);
                    }
                }
            }else {
                //根据主键标识进行案件合并(主要是金额合并)
                for(BaseCaseAllImportExcelTemp baseObj:baseCaseList) {
                    if (resultMap.containsKey(baseObj.getPrimaryKey())) {
                        //开始数据合并(金额合并)
                        BaseCaseAllImportExcelTemp firstObj=resultMap.get(baseObj.getPrimaryKey());
                        firstObj.setOverdueAmtTotal(baseObj.getOverdueAmtTotal()+firstObj.getOverdueAmtTotal());//委案金额(人民币)
                        firstObj.setOverdueAmtTotalDollar(baseObj.getOverdueAmtTotal()+firstObj.getOverdueAmtTotal());//委案金额(美元)
                        firstObj.setCapitalAmt(baseObj.getCapitalAmt()+firstObj.getCapitalAmt());//本金(人民币)
                        firstObj.setCapitalAmtDollar(baseObj.getCapitalAmtDollar()+firstObj.getCapitalAmtDollar());//本金(美元)
                        firstObj.setLeftAmt(baseObj.getLeftAmt()+firstObj.getLeftAmt());//欠款(人民币)
                        firstObj.setLeftAmtDollar(baseObj.getLeftAmtDollar()+firstObj.getLeftAmtDollar());//欠款(美元)
                        firstObj.setInterestAmt(baseObj.getInterestAmt()+firstObj.getInterestAmt());//利息(人民币)
                        firstObj.setInterestAmtDollar(baseObj.getInterestAmtDollar()+firstObj.getInterestAmtDollar());//利息(美元)
                        firstObj.setLateFee(baseObj.getLateFee()+firstObj.getLateFee());//滞纳金(人民币)
                        firstObj.setLateFeeDollar(baseObj.getLateFeeDollar()+firstObj.getLateFeeDollar());//滞纳金(美元)
                        firstObj.setServiceFee(baseObj.getServiceFee()+firstObj.getServiceFee());//服务费(人民币)
                        firstObj.setServiceFeeDollar(baseObj.getServiceFeeDollar()+firstObj.getServiceFeeDollar());//服务费(美元)
                        firstObj.setOverLimitFee(baseObj.getOverLimitFee()+firstObj.getOverLimitFee());//超限费(人民币)
                        firstObj.setOverLimitFeeDollar(baseObj.getOverLimitFeeDollar()+firstObj.getOverLimitFeeDollar());//超限费(美元)
                        firstObj.setFineFee(baseObj.getFineFee()+firstObj.getFineFee());//违约金(人民币)
                        firstObj.setFineFeeDollar(baseObj.getFineFeeDollar()+firstObj.getFineFeeDollar());//违约金(美元)
                        //逾期阶段合并（值一样不做任何处理，不一样的做合并处理）
                        if(StringUtils.isBlank(baseObj.getHandsNumber())){
                            firstObj.getHandsNumberSet().add(baseObj.getHandsNumber());
                        }
                        ids.add(baseObj.getId());
                       //卡信息合并
                        createCardInfoAlone(firstObj,baseObj);
                        createMergeDataModel(importDataExcelRecord, mergeDataModels, baseObj);
                    }else {
                        resultMap.put(baseObj.getPrimaryKey(),baseObj);
                        //卡信息生成
                        Map<String,CardInformation> cardInformationMap=new HashMap<>();
                        createCardInfo(baseObj, cardInformationMap);
                        baseObj.setCardInformationSet(cardInformationMap.values().stream().collect(Collectors.toSet()));
                        //逾期阶段合并
                        Set<String> handsNumberSet=new HashSet<>();
                        handsNumberSet.add(baseObj.getHandsNumber());
                        baseObj.setHandsNumberSet(handsNumberSet);
                        updateList.add(resultMap.get(baseObj.getPrimaryKey()));
                    }
                }
            }
            if(!ids.isEmpty()){
                DeleteByQueryRequestBuilder deleteByQueryRequestBuilder = DeleteByQueryAction.INSTANCE.
                        newRequestBuilder(elasticsearchTemplate.getClient());
                BoolQueryBuilder boolQueryBuilder= QueryBuilders.boolQuery();
                boolQueryBuilder.must(termsQuery("id.keyword", ids));
                deleteByQueryRequestBuilder.source("base_case_all_import_excel_temp").filter(boolQueryBuilder).refresh(true).get();
            }
            if(!updateList.isEmpty()){
                List<List<BaseCaseAllImportExcelTemp>> parts = Lists.partition(updateList, 1000);
                parts.stream().forEach(list ->  baseCaseImportExcelTempRepository.saveAll(list));
            }
            //记录数据合并
            if(!mergeDataModels.isEmpty()){
                List<List<MergeDataModel>> parts = Lists.partition(mergeDataModels, 1000);
                parts.stream().forEach(list ->  mergeDataModelRepository.saveAll(list));
            }
            ids.clear();
            updateList.clear();
            resultMap.clear();
            mergeDataModels.clear();
        }
        logger.info("{} 批次结束卡信息合并........", importDataExcelRecord.getBatchNumber());
    }

    /**
     * 生成卡信息
     * @param baseObj
     */
    private void createCardInfoAlone(BaseCaseAllImportExcelTemp firstObj,BaseCaseAllImportExcelTemp baseObj) {
        Map<String,CardInformation> cardInformationMap=firstObj.getCardInformationSet().stream().collect(
                Collectors.toMap(CardInformation::getCardNo,cardInformation -> cardInformation));
        if(!StringUtils.isBlank(baseObj.getCardNo()) && !cardInformationMap.containsKey(baseObj.getCardNo())){
            CardInformation cardInformation=new CardInformation();
            cardInformation.setCardNo(baseObj.getCardNo());
            cardInformation.setCardNoType(baseObj.getCardNoType());
            cardInformation.setOpenAccountDate(baseObj.getOpenAccountDate());
            cardInformation.setBillDay(baseObj.getBillDay());
            cardInformation.setOverdueDays(baseObj.getOverdueDays());
            cardInformation.setOverduePeriods(baseObj.getOverduePeriods());
            cardInformation.setLatestPayDate(baseObj.getLatestPayDate());
            cardInformation.setCapitalAmt(baseObj.getCapitalAmt());
            cardInformation.setCapitalAmtDollar(baseObj.getCapitalAmtDollar());
            cardInformation.setLeftAmt(baseObj.getLeftAmt());
            cardInformation.setLeftAmtDollar(baseObj.getLeftAmtDollar());
            cardInformation.setInterestAmt(baseObj.getInterestAmt());
            cardInformation.setInterestAmtDollar(baseObj.getInterestAmtDollar());
            cardInformation.setLateFee(baseObj.getLateFee());
            cardInformation.setLateFeeDollar(baseObj.getLateFeeDollar());
            cardInformation.setServiceFee(baseObj.getServiceFee());
            cardInformation.setServiceFeeDollar(baseObj.getServiceFeeDollar());
            cardInformation.setOverLimitFee(baseObj.getOverLimitFee());
            cardInformation.setOverLimitFeeDollar(baseObj.getOverLimitFeeDollar());
            cardInformation.setFineFee(baseObj.getFineFee());
            cardInformation.setFineFeeDollar(baseObj.getFineFeeDollar());
            cardInformation.setMinPayAmt(baseObj.getMinPayAmt());
            cardInformation.setMinPayAmtDollar(baseObj.getMinPayAmtDollar());
            cardInformation.setLatestPayAmt(baseObj.getLatestPayAmt());
            cardInformation.setLatestPayAmtDollar(baseObj.getLatestPayAmtDollar());
            cardInformation.setStopAccountDate(baseObj.getStopAccountDate());
            cardInformation.setLastConsumptionDate(baseObj.getLastConsumptionDate());
            cardInformation.setLastPresentationDate(baseObj.getLastPresentationDate());
            cardInformation.setLastDefaultDate(baseObj.getLastDefaultDate());
            cardInformation.setFreeDate(baseObj.getFreeDate());
            cardInformation.setLimitAmt(baseObj.getLimitAmt());
            firstObj.getCardInformationSet().add(cardInformation);
        }
    }

    /**
     * 生成卡信息
     * @param baseObj
     * @param cardInformationSet
     */
    private void createCardInfo(BaseCaseAllImportExcelTemp baseObj, Map<String, CardInformation> cardInformationSet) {
        if(!StringUtils.isBlank(baseObj.getCardNo()) && !cardInformationSet.containsKey(baseObj.getCardNo())){
            CardInformation cardInformation=new CardInformation();
            cardInformation.setCardNo(baseObj.getCardNo());
            cardInformation.setCardNoType(baseObj.getCardNoType());
            cardInformation.setOpenAccountDate(baseObj.getOpenAccountDate());
            cardInformation.setBillDay(baseObj.getBillDay());
            cardInformation.setOverdueDays(baseObj.getOverdueDays());
            cardInformation.setOverduePeriods(baseObj.getOverduePeriods());
            cardInformation.setLatestPayDate(baseObj.getLatestPayDate());
            cardInformation.setCapitalAmt(baseObj.getCapitalAmt());
            cardInformation.setCapitalAmtDollar(baseObj.getCapitalAmtDollar());
            cardInformation.setLeftAmt(baseObj.getLeftAmt());
            cardInformation.setLeftAmtDollar(baseObj.getLeftAmtDollar());
            cardInformation.setInterestAmt(baseObj.getInterestAmt());
            cardInformation.setInterestAmtDollar(baseObj.getInterestAmtDollar());
            cardInformation.setLateFee(baseObj.getLateFee());
            cardInformation.setLateFeeDollar(baseObj.getLateFeeDollar());
            cardInformation.setServiceFee(baseObj.getServiceFee());
            cardInformation.setServiceFeeDollar(baseObj.getServiceFeeDollar());
            cardInformation.setOverLimitFee(baseObj.getOverLimitFee());
            cardInformation.setOverLimitFeeDollar(baseObj.getOverLimitFeeDollar());
            cardInformation.setFineFee(baseObj.getFineFee());
            cardInformation.setFineFeeDollar(baseObj.getFineFeeDollar());
            cardInformation.setMinPayAmt(baseObj.getMinPayAmt());
            cardInformation.setMinPayAmtDollar(baseObj.getMinPayAmtDollar());
            cardInformation.setLatestPayAmt(baseObj.getLatestPayAmt());
            cardInformation.setLatestPayAmtDollar(baseObj.getLatestPayAmtDollar());
            cardInformation.setStopAccountDate(baseObj.getStopAccountDate());
            cardInformation.setLastConsumptionDate(baseObj.getLastConsumptionDate());
            cardInformation.setLastPresentationDate(baseObj.getLastPresentationDate());
            cardInformation.setLastDefaultDate(baseObj.getLastDefaultDate());
            cardInformation.setFreeDate(baseObj.getFreeDate());
            cardInformation.setLimitAmt(baseObj.getLimitAmt());
            cardInformationSet.put(baseObj.getCardNo(),cardInformation);
        }
    }

    /**
     * 记录数据合并
     * @param importDataExcelRecord
     * @param mergeDataModels
     * @param firstObj
     */
    private void createMergeDataModel(ImportDataExcelRecord importDataExcelRecord, List<MergeDataModel> mergeDataModels, BaseCaseAllImportExcelTemp firstObj) {
        MergeDataModel mergeDataModel=new MergeDataModel();
        BeanUtils.copyProperties(firstObj,mergeDataModel);
        mergeDataModel.setRecordId(importDataExcelRecord.getId());
        mergeDataModels.add(mergeDataModel);
    }
}

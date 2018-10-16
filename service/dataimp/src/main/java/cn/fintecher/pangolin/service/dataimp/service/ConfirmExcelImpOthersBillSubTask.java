package cn.fintecher.pangolin.service.dataimp.service;

import cn.fintecher.pangolin.common.enums.CaseDataStatus;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.domain.*;
import cn.fintecher.pangolin.entity.managentment.Operator;
import cn.fintecher.pangolin.service.dataimp.repository.BalancePayRecordRepository;
import cn.fintecher.pangolin.service.dataimp.repository.ImpPayAmtLogRepository;
import cn.fintecher.pangolin.service.dataimp.repository.ImportBaseCaseRepository;
import com.google.common.collect.Lists;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

/**
 * @Author:peishouwen
 * @Desc: 案件对账确认
 * @Date:Create in 22:37 2018/8/6
 */
@Service("confirmExcelImpOthersBillSubTask")
public class ConfirmExcelImpOthersBillSubTask {
    Logger logger = LoggerFactory.getLogger(ConfirmExcelImpOthersBillSubTask.class);

    @Autowired
    ImportBaseCaseRepository importBaseCaseRepository;

    @Autowired
    BalancePayRecordRepository balancePayRecordRepository;

    @Autowired
    ImpPayAmtLogRepository impPayAmtLogRepository;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Async
    public CompletableFuture<Long> doSubTask(List<CaseBillImportTemp> caseBillImportTempList, int pageNo,OperatorModel operator) {
        logger.info("开始进行案件对账更新第{}页数据,确认数据量:{}",pageNo,caseBillImportTempList.size());
        List<BaseCase> caseList=new ArrayList<>();
        List<PayAmtLog> payAmtLogList = new ArrayList<>();
        List<BalancePayRecord> balancePayRecordList = new ArrayList<>();
        Set<String> deleteIds=new HashSet<>();
        Set<String> primaryKeySets=new HashSet<>();
        Long total=0L;
        try {
            //根据配置的对应规则和更新日期（如果日期小于原有的更新日期则不更新），更新案件还款总金额,及余额
            //根据primaryKey一次性查询所有需要更新的数据
            for(CaseBillImportTemp obj:caseBillImportTempList){
                primaryKeySets.add(obj.getPrimaryKey());
            }
            BoolQueryBuilder qb= QueryBuilders.boolQuery();
            qb.must(termsQuery("primaryKey.keyword",primaryKeySets));
            Iterable<BaseCase> baseCaseIterable=importBaseCaseRepository.search(qb);
            List<BaseCase> baseCaseList= Lists.newArrayList(baseCaseIterable);
            if(!baseCaseList.isEmpty()) {
                //案件信息转化为数组
                Map<String, BaseCase> baseCaseMap = baseCaseList.stream().collect(Collectors.toMap(BaseCase::getPrimaryKey, baseCase -> baseCase));
                //根据主键信息分组
                Map<String,List<CaseBillImportTemp>> CaseBillImportTempMap=caseBillImportTempList.stream().collect(Collectors.groupingBy(CaseBillImportTemp::getPrimaryKey));
                CaseBillImportTempMap.forEach((String key, List<CaseBillImportTemp> list) ->{
                    //一组更新信息
                    BaseCase baseCase=baseCaseMap.get(key);
                    if(Objects.nonNull(baseCase)){
                        //获取案件对应的卡信息
                     Set<CardInformation> cardInformationSet=  baseCase.getCardInformationSet();
                     //set转为Map
                     Map<String,CardInformation>  cardInformationMap=cardInformationSet.stream().collect(Collectors.toMap(CardInformation::getCardNo,cardInformation ->cardInformation));
                        list.forEach(obj->{
                            deleteIds.add(obj.getId());
                            if(cardInformationMap.containsKey(obj.getCardNo())){
                                CardInformation temp=  cardInformationMap.get(obj.getCardNo());
                                if(Objects.isNull(temp.getLatelyUpdateDate()) ||
                                        ZWDateUtil.getBetween(temp.getLatelyUpdateDate(),obj.getUpdateDate(), ChronoUnit.DAYS)<0){
                                    PayAmtLog payAmtLog = new PayAmtLog();
                                    payAmtLog.setCaseId(baseCase.getId());
                                    payAmtLog.setLatestPayDate(obj.getUpdateDate());
                                    payAmtLog.setCardNo(obj.getCardNo());
                                    payAmtLog.setOperator(operator.getUsername());
                                    payAmtLog.setOperatorName(operator.getFullName());
                                    payAmtLog.setOperatorTime(ZWDateUtil.getNowDateTime());
                                    payAmtLogList.add(payAmtLog);
                                    if (Objects.nonNull(obj.getLeftAmt())) {
                                        temp.setLeftAmt(obj.getLeftAmt());
                                        payAmtLog.setLeftAmt(obj.getLeftAmt());
                                    }
                                    if (Objects.nonNull(obj.getLeftAmtDollar())) {
                                        temp.setLeftAmtDollar(obj.getLeftAmtDollar());
                                        payAmtLog.setLeftAmtDollar(obj.getLeftAmtDollar());
                                    }
                                    if (Objects.nonNull(obj.getPayAmountTotal())) {
                                        temp.setPayAmountTotal(obj.getPayAmountTotal());
                                        payAmtLog.setLatestPayAmt(obj.getPayAmountTotal());
                                    }
                                    if (Objects.nonNull(obj.getPayAmountTotalDollar())) {
                                        temp.setPayAmountTotalDollar(obj.getPayAmountTotalDollar());
                                        payAmtLog.setLatestPayAmtDollar(obj.getPayAmountTotalDollar());
                                    }
                                    //对账还款记录
                                    balancePayRecordList.add( createBalanceRecord(baseCase, obj));
                                }
                            }
                        });
                        //更新案件中汇总金额信息
                        cardInformationMap.forEach((cardNo,value)->{
                            baseCase.setLeftAmt(baseCase.getLeftAmt()+value.getLeftAmt());
                            baseCase.setLeftAmtDollar(baseCase.getLeftAmtDollar()+value.getLeftAmtDollar());
                            baseCase.setPayAmountTotal(baseCase.getPayAmountTotal()+value.getPayAmountTotal());
                            baseCase.setPayAmountTotalDollar(baseCase.getOverdueAmtTotalDollar()+value.getPayAmountTotal());
                            if(Objects.isNull(baseCase.getLatelyUpdateDate()) ||
                                    ZWDateUtil.getBetween(baseCase.getLatelyUpdateDate(),value.getLatelyUpdateDate(), ChronoUnit.DAYS)<0){
                                baseCase.setLatelyUpdateDate(value.getLatelyUpdateDate());
                            }
                        });
                        //更新卡信息
                        baseCase.setCardInformationSet(cardInformationMap.values().stream().collect(Collectors.toSet()));
                        //结清状态设置(余额为0时案件状态设置为结清)
                        if(baseCase.getLeftAmt().compareTo(Double.parseDouble("0.0"))==0 &&
                                baseCase.getLeftAmtDollar().compareTo(Double.parseDouble("0.0"))==0){
                            baseCase.setCaseDataStatus(CaseDataStatus.SETTLT);
                        }
                        caseList.add(baseCase);
                    }
                });
            }
            if(!caseList.isEmpty()){
                importBaseCaseRepository.saveAll(caseList);
            }
            if(!payAmtLogList.isEmpty()){
                impPayAmtLogRepository.saveAll(payAmtLogList);
            }
            if(!balancePayRecordList.isEmpty()){
                balancePayRecordRepository.saveAll(balancePayRecordList);
            }
            //删除已经更新的数据
            if(!deleteIds.isEmpty()){
                total=total+deleteIds.size();
                BoolQueryBuilder queryBuilder= QueryBuilders.boolQuery();
                queryBuilder.must().add(termsQuery("id.keyword",deleteIds));
                DeleteByQueryRequestBuilder deleteByQueryRequestBuilder= DeleteByQueryAction.INSTANCE.
                        newRequestBuilder(elasticsearchTemplate.getClient());
                deleteByQueryRequestBuilder.source("case_bill_import_temp").filter(queryBuilder).refresh(true).execute();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }finally {
            caseList.clear();
            payAmtLogList.clear();
            balancePayRecordList.clear();
            deleteIds.clear();
        }
        logger.info("完成进行案件对账第{}页数据", pageNo);
        return CompletableFuture.completedFuture(total);
    }

    /**
     * 此处需要调整
     * @param baseCase
     * @param obj
     * @return
     */
    private BalancePayRecord createBalanceRecord(BaseCase baseCase, CaseBillImportTemp obj) {
        try {
            BalancePayRecord record = new BalancePayRecord();
            if (baseCase.getLeftAmt() - obj.getLeftAmt() > 0) {
                record.setCaseId(baseCase.getId());
                record.setPayAmt(baseCase.getLeftAmt() - obj.getLeftAmt());
                record.setPayDate(obj.getUpdateDate());
                record.setOrganizationName(baseCase.getDetaptName());
                if (Objects.nonNull(baseCase.getCurrentCollector())) {
                    Operator collector = baseCase.getCurrentCollector();
                    record.setCollectorId(collector.getId());
                    record.setCollectorName(collector.getFullName());
                    record.setEmployeeNumber(collector.getEmployeeNumber());
                }
                return record;
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }finally {
            return null;
        }
    }

}

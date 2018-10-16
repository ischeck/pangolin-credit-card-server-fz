package cn.fintecher.pangolin.service.dataimp.service;

import cn.fintecher.pangolin.common.enums.CaseDataStatus;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.domain.*;
import cn.fintecher.pangolin.service.dataimp.repository.ImpLeftAmtLogRepPository;
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
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

/**
 * @Author:peishouwen
 * @Desc: 案件更新确认
 * @Date:Create in 22:37 2018/8/6
 */
@Service("confirmExcelImpOthersUpdateSubTask")
public class ConfirmExcelImpOthersUpdateSubTask {
    Logger logger= LoggerFactory.getLogger(ConfirmExcelImpOthersUpdateSubTask.class);

    @Autowired
    ImportBaseCaseRepository importBaseCaseRepository;

    @Autowired
    ImpLeftAmtLogRepPository impLeftAmtLogRepPository;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Async
    public CompletableFuture<Long> doSubTask(List<CaseUpdateImportTemp> caseUpdateImportTempList, int pageNo,OperatorModel operator){
        logger.info("开始进行案件余额更新第{}页数据,确认数据量:{}",pageNo,caseUpdateImportTempList.size());
        List<BaseCase> caseList=new ArrayList<>();
        List<LeftAmtLog> leftAmtLogList=new ArrayList<>();
        Set<String> primaryKeySets=new HashSet<>();
        Set<String> deleteIds=new HashSet<>();
        Long total=0L;
        try {
            //根据配置的对应规则和更新日期（如果日期小于原有的更新日期则不更新），更新案件余额
            //根据primaryKey一次性查询所有需要更新的数据
            for(CaseUpdateImportTemp obj:caseUpdateImportTempList){
                primaryKeySets.add(obj.getPrimaryKey());
            }
            BoolQueryBuilder qb= QueryBuilders.boolQuery();
            qb.must(termsQuery("primaryKey.keyword",primaryKeySets));
            Iterable<BaseCase> baseCaseIterable=importBaseCaseRepository.search(qb);
            List<BaseCase> baseCaseList= Lists.newArrayList(baseCaseIterable);
            if(!baseCaseList.isEmpty()){
                //案件信息转化为数组
                Map<String,BaseCase> baseCaseMap=baseCaseList.stream().collect(Collectors.toMap(BaseCase ::getPrimaryKey,baseCase -> baseCase));
                //根据主键信息分组
                Map<String,List<CaseUpdateImportTemp>> CaseBillImportTempMap=caseUpdateImportTempList.stream().collect(Collectors.groupingBy(CaseUpdateImportTemp::getPrimaryKey));
                CaseBillImportTempMap.forEach((key,list)->{
                    //一组更新信息
                    BaseCase baseCase=baseCaseMap.get(key);
                    if(Objects.nonNull(baseCase)) {
                        //获取案件对应的卡信息
                        Set<CardInformation> cardInformationSet = baseCase.getCardInformationSet();
                        //转为Map
                        Map<String,CardInformation>  cardInformationMap=cardInformationSet.stream().collect(Collectors.toMap(CardInformation::getCardNo,cardInformation ->cardInformation));
                        list.forEach(obj->{
                            deleteIds.add(obj.getId());
                            if(cardInformationMap.containsKey(obj.getCardNo())){
                                CardInformation temp=  cardInformationMap.get(obj.getCardNo());
                                if(Objects.isNull(temp.getLatelyUpdateDate()) ||
                                        ZWDateUtil.getBetween(temp.getLatelyUpdateDate(),obj.getUpdateDate(), ChronoUnit.DAYS)<0){
                                    LeftAmtLog leftAmtLog=new LeftAmtLog();
                                    leftAmtLog.setCaseId(baseCase.getId());
                                    leftAmtLog.setCardNo(obj.getCardNo());
                                    leftAmtLog.setLatelyUpdateDate(obj.getUpdateDate());
                                    leftAmtLog.setOperator(operator.getUsername());
                                    leftAmtLog.setOperatorName(operator.getFullName());
                                    leftAmtLog.setOperatorTime(ZWDateUtil.getNowDateTime());
                                    if(Objects.nonNull(obj.getLeftAmt())){
                                        temp.setLeftAmt(obj.getLeftAmt());
                                        leftAmtLog.setLeftAmt(obj.getLeftAmt());
                                    }
                                    if(Objects.nonNull(obj.getLeftAmtDollar())){
                                        temp.setLeftAmtDollar(obj.getLeftAmtDollar());
                                        leftAmtLog.setLeftAmtDollar(obj.getLeftAmtDollar());
                                    }
                                    leftAmtLogList.add(leftAmtLog);
                                }
                            }
                        });
                        //更新案件中汇总金额信息
                        cardInformationMap.forEach((cardNo,value)->{
                            baseCase.setLeftAmt(baseCase.getLeftAmt()+value.getLeftAmt());
                            baseCase.setLeftAmtDollar(baseCase.getLeftAmtDollar()+value.getLeftAmtDollar());
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
                List<List<BaseCase>> parts = Lists.partition(caseList, 1000);
                parts.stream().forEach(list ->  importBaseCaseRepository.saveAll(list));
            }
            if(!leftAmtLogList.isEmpty()){
                List<List<LeftAmtLog>> parts = Lists.partition(leftAmtLogList, 1000);
                parts.stream().forEach(list ->  impLeftAmtLogRepPository.saveAll(list));
            }
            //删除已经更新的数据
            if(!deleteIds.isEmpty()){
                total=total+deleteIds.size();
                BoolQueryBuilder queryBuilder= QueryBuilders.boolQuery();
                queryBuilder.must().add(termsQuery("id.keyword",deleteIds));
                DeleteByQueryRequestBuilder deleteByQueryRequestBuilder= DeleteByQueryAction.INSTANCE.
                        newRequestBuilder(elasticsearchTemplate.getClient());
                deleteByQueryRequestBuilder.source("case_update_import_temp").filter(queryBuilder).refresh(true).execute();
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }finally {
            caseList.clear();
            leftAmtLogList.clear();
            primaryKeySets.clear();
        }
        logger.info("完成进行案件余额更新第{}页数据",pageNo);
        return   CompletableFuture.completedFuture(total);
    }
}

package cn.fintecher.pangolin.service.dataimp.service;

import cn.fintecher.pangolin.common.enums.AssistStatus;
import cn.fintecher.pangolin.common.enums.CaseIssuedFlag;
import cn.fintecher.pangolin.common.enums.DistributeWay;
import cn.fintecher.pangolin.common.enums.ImportDataExcelStatus;
import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.response.LoginResponse;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.domain.AssistCollectionCase;
import cn.fintecher.pangolin.entity.domain.BaseCase;
import cn.fintecher.pangolin.entity.domain.CaseTransferLog;
import cn.fintecher.pangolin.entity.domain.ImportDataExcelRecord;
import cn.fintecher.pangolin.entity.managentment.Operator;
import cn.fintecher.pangolin.entity.managentment.Organization;
import cn.fintecher.pangolin.service.dataimp.client.OrganizationClient;
import cn.fintecher.pangolin.service.dataimp.model.request.*;
import cn.fintecher.pangolin.service.dataimp.model.response.*;
import cn.fintecher.pangolin.service.dataimp.repository.CaseTransferLogRepository;
import cn.fintecher.pangolin.service.dataimp.repository.ImpAssistCaseRepository;
import cn.fintecher.pangolin.service.dataimp.repository.ImportBaseCaseRepository;
import cn.fintecher.pangolin.service.dataimp.repository.ImportDataExcelRecordRepository;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

/**
 * @Author:peishouwen
 * @Desc: 案件手工分配
 * @Date:Create in 16:50 2018/8/9
 */
@Service("distributeCaseService")
public class DistributeCaseService {

    Logger logger = LoggerFactory.getLogger(DistributeCaseService.class);

    @Autowired
    ImportBaseCaseRepository importBaseCaseRepository;

    @Autowired
    OrganizationClient organizationClient;

    @Autowired
    ImpAssistCaseRepository impAssistCaseRepository;

    @Autowired
    DataimpBaseService dataimpBaseService;

    @Autowired
    CaseTransferLogRepository caseTransferLogRepository;

    /**
     * 个人案件分配展示
     *
     * @param request
     * @return
     */
    public GroupCaseDistributeResponse manualDistributeCase(GroupCaseDistributeRequest request) {
        GroupCaseDistributeResponse response = new GroupCaseDistributeResponse();
        if (request.getCaseIds().isEmpty()) {
            throw new BadRequestException(null, "manualDistributeCase", "manualDistributeCase.case.isNull");
        }
        if (request.getDistributeConfigModels().isEmpty()) {
            throw new BadRequestException(null, "manualDistributeCase", "manualDistributeCase.user.isNull");
        }
        response.setCaseAmtTotal(request.getCaseAmtTotal());
        response.setCaseNumTotal(request.getCaseNumTotal());
        response.setCollectorTotal(request.getCollectorTotal());
        response.setDistributeWay(request.getDistributeWay());
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if(!request.getCaseIds().isEmpty()){
            queryBuilder.must(termsQuery("id.keyword", request.getCaseIds()));
        }
        if(!StringUtils.isBlank(request.getBatchNumber())){
            queryBuilder.must(matchPhraseQuery("batchNumber.keyword",request.getBatchNumber()));
        }
        Iterable<BaseCase> baseCaseIterable = importBaseCaseRepository.search(queryBuilder);
        LinkedList<BaseCase> baseCaseList = Lists.newLinkedList(baseCaseIterable);
        //将案件按金额从大到小排序
        baseCaseList.sort((BaseCase base1, BaseCase base2) -> base1.getLeftAmt().compareTo(base1.getLeftAmt()));
        List<DistributeConfigModel> distributeConfigModelsReq = request.getDistributeConfigModels();
        if (request.getDistributeWay().equals(DistributeWay.MANUAL_WAY)) {
            //手工分案
            List<DistributeConfigResModel> distributeResults = new ArrayList<>();
            proManualDistribute(distributeConfigModelsReq, baseCaseList, request.getCaseNumTotal(), request.getCaseAmtTotal(), distributeResults);
            response.setDistributeConfigModels(distributeResults);
        } else {
            //平均金额
            double leftAmtAvg = new BigDecimal(request.getCaseAmtTotal()).
                    divide(new BigDecimal(request.getDistributeConfigModels().size()), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
            //平均案件数
            long caseTotalsAvg = request.getCaseNumTotal() / request.getCollectorTotal();
            List<DistributeConfigResModel> distributeResults = new ArrayList<>();
            proSysDistribute(distributeConfigModelsReq, baseCaseList, leftAmtAvg, caseTotalsAvg, request.getCaseNumTotal(), request.getCaseAmtTotal(), distributeResults);
            response.setDistributeConfigModels(distributeResults);
        }
        return response;
    }

    /**
     * 个人案件批量分配展示
     *
     * @param request
     * @return
     */
    public GroupBatchDistributeResponse groupDistributeBatchCase(GroupBatchDistributeRequest request) {
        GroupBatchDistributeResponse response = new GroupBatchDistributeResponse();
        if (request.getDistributeConfigModels().isEmpty()) {
            throw new BadRequestException(null, "manualDistributeCase", "manualDistributeCase.user.isNull");
        }
        response.setCaseAmtTotal(request.getCaseAmtTotal());
        response.setCaseNumTotal(request.getCaseNumTotal());
        response.setCollectorTotal(request.getCollectorTotal());
        response.setDistributeWay(request.getDistributeWay());
        response.setBatchNumber(request.getBatchNumber());
        response.setCitys(request.getCitys());
        response.setDeparts(request.getDeparts());
        Iterable<BaseCase> baseCaseIterable = importBaseCaseRepository.search(request.generateQueryBuilder());
        LinkedList<BaseCase> baseCaseList = Lists.newLinkedList(baseCaseIterable);
        //将案件按金额从大到小排序
        baseCaseList.sort((BaseCase base1, BaseCase base2) -> base1.getLeftAmt().compareTo(base1.getLeftAmt()));
        List<DistributeConfigModel> distributeConfigModelsReq = request.getDistributeConfigModels();
        if (request.getDistributeWay().equals(DistributeWay.MANUAL_WAY)) {
            //手工分案
            List<DistributeConfigResModel> distributeResults = new ArrayList<>();
            proManualDistribute(distributeConfigModelsReq, baseCaseList, request.getCaseNumTotal(), request.getCaseAmtTotal(), distributeResults);
            response.setDistributeConfigModels(distributeResults);
        } else {
            //平均金额
            double leftAmtAvg = new BigDecimal(request.getCaseAmtTotal()).
                    divide(new BigDecimal(request.getDistributeConfigModels().size()), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
            //平均案件数
            long caseTotalsAvg = request.getCaseNumTotal() / request.getCollectorTotal();
            List<DistributeConfigResModel> distributeResults = new ArrayList<>();
            proSysDistribute(distributeConfigModelsReq, baseCaseList, leftAmtAvg, caseTotalsAvg, request.getCaseNumTotal(), request.getCaseAmtTotal(), distributeResults);
            response.setDistributeConfigModels(distributeResults);
        }
        return response;
    }

    /**
     * 区域案件分配展示
     *
     * @param request
     * @return
     */
    public AreaCaseDistributeResponse manualAreaDistributeCase(AreaCaseDistributeRequest request) {
        AreaCaseDistributeResponse response = new AreaCaseDistributeResponse();
        if (request.getCaseIds().isEmpty()) {
            throw new BadRequestException(null, "manualDistributeCase", "manualDistributeCase.case.isNull");
        }
        if (request.getDistributeConfigModels().isEmpty()) {
            throw new BadRequestException(null, "manualDistributeCase", "manualDistributeCase.user.isNull");
        }
        response.setCaseAmtTotal(request.getCaseAmtTotal());
        response.setCaseNumTotal(request.getCaseNumTotal());
        response.setCollectorTotal(request.getCollectorTotal());
        response.setDistributeWay(request.getDistributeWay());
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if(!request.getCaseIds().isEmpty()){
            queryBuilder.must(termsQuery("id.keyword", request.getCaseIds()));
        }
        if(!StringUtils.isBlank(request.getBatchNumber())){
            queryBuilder.must(matchPhraseQuery("batchNumber.keyword",request.getBatchNumber()));
        }
        Iterable<BaseCase> baseCaseIterable = importBaseCaseRepository.search(queryBuilder);
        LinkedList<BaseCase> baseCaseList = Lists.newLinkedList(baseCaseIterable);
        if (request.getDistributeWay().equals(DistributeWay.MANUAL_WAY)) {
            //手工分案
            List<DistributeConfigModel> distributeConfigModelsReq = request.getDistributeConfigModels();
            List<DistributeConfigResModel> distributeResults = new ArrayList<>();
            proManualDistribute(distributeConfigModelsReq, baseCaseList, request.getCaseNumTotal(), request.getCaseAmtTotal(), distributeResults);
            response.setDistributeConfigModels(distributeResults);
        }else{
            //将案件按金额从大到小排序
            baseCaseList.sort((BaseCase base1, BaseCase base2) -> base1.getLeftAmt().compareTo(base1.getLeftAmt()));
            //平均金额
            double leftAmtAvg = new BigDecimal(request.getCaseAmtTotal()).
                    divide(new BigDecimal(request.getDistributeConfigModels().size()), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
            //平均案件数
            long caseTotalsAvg = request.getCaseNumTotal() / request.getCollectorTotal();
            if (caseTotalsAvg == 0) {
                caseTotalsAvg = 1;
            }
            List<DistributeConfigModel> distributeConfigModelsReq = request.getDistributeConfigModels();
            List<DistributeConfigResModel> distributeResults = new ArrayList<>();
            proSysDistribute(distributeConfigModelsReq, baseCaseList, leftAmtAvg, caseTotalsAvg, request.getCaseNumTotal(), request.getCaseAmtTotal(), distributeResults);
            response.setDistributeConfigModels(distributeResults);
        }
        return response;
    }

    /**
     * 区域案件批量分配展示
     *
     * @param request
     * @return
     */
    public AreaCaseDistributeBatchResponse areaDistributeBatchCase(AreaCaseDistributeBatchRequest request) {
        AreaCaseDistributeBatchResponse response = new AreaCaseDistributeBatchResponse();
        response.setCaseAmtTotal(request.getCaseAmtTotal());
        response.setCaseNumTotal(request.getCaseNumTotal());
        response.setCollectorTotal(request.getCollectorTotal());
        response.setDistributeWay(request.getDistributeWay());
        response.setBatchNumber(request.getBatchNumber());
        request.setCitys(request.getCitys());
        BoolQueryBuilder queryBuilder = request.generateQueryBuilder();
        Iterable<BaseCase> baseCaseIterable = importBaseCaseRepository.search(queryBuilder);
        LinkedList<BaseCase> baseCaseList = Lists.newLinkedList(baseCaseIterable);
        if (request.getDistributeWay().equals(DistributeWay.MANUAL_WAY)) {
            //手工分案
            List<DistributeConfigModel> distributeConfigModelsReq = request.getDistributeConfigModels();
            List<DistributeConfigResModel> distributeResults = new ArrayList<>();
            proManualDistribute(distributeConfigModelsReq, baseCaseList, request.getCaseNumTotal(), request.getCaseAmtTotal(), distributeResults);
            response.setDistributeConfigModels(distributeResults);
        }else {
            //将案件按金额从大到小排序
            baseCaseList.sort((BaseCase base1, BaseCase base2) -> base1.getLeftAmt().compareTo(base1.getLeftAmt()));
            //平均金额
            double leftAmtAvg = new BigDecimal(request.getCaseAmtTotal()).
                    divide(new BigDecimal(request.getDistributeConfigModels().size()), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
            //平均案件数
            long caseTotalsAvg = request.getCaseNumTotal() / request.getCollectorTotal();
            if (caseTotalsAvg == 0) {
                caseTotalsAvg = 1;
            }
            List<DistributeConfigModel> distributeConfigModelsReq = request.getDistributeConfigModels();
            List<DistributeConfigResModel> distributeResults = new ArrayList<>();
            proSysDistribute(distributeConfigModelsReq, baseCaseList, leftAmtAvg, caseTotalsAvg, request.getCaseNumTotal(), request.getCaseAmtTotal(), distributeResults);
            response.setDistributeConfigModels(distributeResults);
        }
        return response;
    }


    /**
     * 外访案件分配展示
     *
     * @param request
     * @return
     */
    public GroupCaseDistributeResponse manualAssistDistributeCase(GroupCaseDistributeRequest request) {
        GroupCaseDistributeResponse response = new GroupCaseDistributeResponse();
        if (request.getCaseIds().isEmpty()) {
            throw new BadRequestException(null, "manualDistributeCase", "manualDistributeCase.case.isNull");
        }
        if (request.getDistributeConfigModels().isEmpty()) {
            throw new BadRequestException(null, "manualDistributeCase", "manualDistributeCase.user.isNull");
        }
        response.setCaseNumTotal(request.getCaseNumTotal());
        response.setCollectorTotal(request.getCollectorTotal());
        response.setDistributeWay(request.getDistributeWay());
        List<DistributeConfigResModel> distributeResults = new ArrayList<>();
        for (DistributeConfigModel reqModel : request.getDistributeConfigModels()) {
            DistributeConfigResModel resModel = new DistributeConfigResModel();
            BeanUtils.copyProperties(reqModel, resModel);
            distributeResults.add(resModel);
        }
        proAssistSysDistribute(request.getCaseIds(), distributeResults, request.getCaseNumTotal());
        response.setDistributeConfigModels(distributeResults);

        return response;
    }

    /**
     * 分案规则如下：
     * 1、案件接收人做第一级循环，将案件金额从大到小顺序排列；
     * 2、计算接收人可接受案件数量及金额平均值，每次取队列中最大值和最小值，给接收人；
     * 3、当取最大值时已经超过数量或金额平均中的任何一个则最小值由新接收人接收案件。依次类推；
     * 4、如果所有接收人都到达最大值限制,则接收人案件数量从下到大排序，将剩余案子再次算平均值，依次增加给案件接收人；
     *
     * @param distributeConfigModelList
     * @param baseCaseList
     * @param leftAmtAvg
     * @param caseTotalsAvg
     * @param caseNumTotal
     * @param caseAmtTotal
     * @param distributeResults
     */
    public void proSysDistribute(List<DistributeConfigModel> distributeConfigModelList, LinkedList<BaseCase> baseCaseList,
                                 double leftAmtAvg, long caseTotalsAvg, long caseNumTotal, double caseAmtTotal,
                                 List<DistributeConfigResModel> distributeResults) {
        //案件接收为一时,案件直接分配
        if (distributeConfigModelList.size() == 1) {
            //催收员分配数据
            DistributeConfigModel req = distributeConfigModelList.get(0);
            DistributeConfigResModel resModel = new DistributeConfigResModel();
            BeanUtils.copyProperties(req, resModel);
            resModel.setLimitAmt(leftAmtAvg);
            resModel.setLimitNum(caseTotalsAvg);
            resModel.setPerNum(1.0);
            //分配案件数
            resModel.setCaseNumTotal(caseNumTotal);
            //分配金额数
            resModel.setLeftAmtTotal(caseAmtTotal);
            for (BaseCase obj : baseCaseList) {
                resModel.getResultMap().put(obj.getId(), obj.getLeftAmt());
            }
            distributeResults.add(resModel);
        } else {
            for (DistributeConfigModel req : distributeConfigModelList) {
                DistributeConfigResModel resModel = new DistributeConfigResModel();
                BeanUtils.copyProperties(req, resModel);
                resModel.setLimitAmt(leftAmtAvg);
                resModel.setLimitNum(caseTotalsAvg);
                while (true) {
                    if (baseCaseList.isEmpty()) {
                        double perNum = new BigDecimal(resModel.getCaseNumTotal()).divide(new BigDecimal(caseNumTotal), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        resModel.setPerNum(perNum);
                        distributeResults.add(resModel);
                        break;
                    }
                    //取低位值
                    BaseCase low = baseCaseList.getFirst();
                    //分配案件数
                    resModel.setCaseNumTotal(resModel.getCaseNumTotal() + 1);
                    //分配金额数
                    resModel.setLeftAmtTotal(new BigDecimal(low.getLeftAmt() + resModel.getLeftAmtTotal()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                    resModel.getResultMap().put(low.getId(), low.getLeftAmt());
                    baseCaseList.remove(low);
                    //判断是否满足限制条件
                    if (resModel.getLeftAmtTotal() >= leftAmtAvg || resModel.getCaseNumTotal() >= caseTotalsAvg) {
                        double perNum = new BigDecimal(resModel.getCaseNumTotal()).divide(new BigDecimal(caseNumTotal), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        resModel.setPerNum(perNum);
                        distributeResults.add(resModel);
                        //取下一个催员
                        break;
                    } else {
                        //取高位
                        if (!baseCaseList.isEmpty()) {
                            BaseCase high = baseCaseList.getLast();
                            //分配数量
                            resModel.setCaseNumTotal(resModel.getCaseNumTotal() + 1);
                            //分配金额
                            resModel.setLeftAmtTotal(new BigDecimal(high.getLeftAmt() + resModel.getLeftAmtTotal()).setScale(2,
                                    BigDecimal.ROUND_HALF_UP).doubleValue());
                            resModel.getResultMap().put(high.getId(), high.getLeftAmt());
                            baseCaseList.remove(high);
                            //判断是否满足限制条件
                            if (resModel.getLeftAmtTotal() >= leftAmtAvg || resModel.getCaseNumTotal() >= caseTotalsAvg) {
                                double perNum = new BigDecimal(resModel.getCaseNumTotal()).divide(new BigDecimal(caseNumTotal), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                resModel.setPerNum(perNum);
                                distributeResults.add(resModel);
                                break;
                            }
                        }
                    }
                }
            }
            //处理剩余未分配的案子
            if (!baseCaseList.isEmpty()) {
                distributeLeafList(baseCaseList, distributeResults, caseNumTotal);
            }
        }
    }

    /**
     * 执行案件分配(手动分案)
     *
     * @param distributeConfigModelList 催收员数据
     * @param baseCaseList              案件数据
     */
    public void proManualDistribute(List<DistributeConfigModel> distributeConfigModelList, LinkedList<BaseCase> baseCaseList,
                                    long caseNumTotal, double caseAmtTotal, List<DistributeConfigResModel> distributeResults) {
        //案件接收为一时,案件直接分配
        if (distributeConfigModelList.size() == 1) {
            DistributeConfigModel req = distributeConfigModelList.get(0);
            DistributeConfigResModel resModel = new DistributeConfigResModel();
            BeanUtils.copyProperties(req, resModel);
            resModel.setPerNum(1.0);
            //分配案件数
            resModel.setCaseNumTotal(caseNumTotal);
            //分配金额数
            resModel.setLeftAmtTotal(caseAmtTotal);
            for (BaseCase obj : baseCaseList) {
                resModel.getResultMap().put(obj.getId(), obj.getLeftAmt());
            }
            distributeResults.add(resModel);
        } else {
            for (DistributeConfigModel req : distributeConfigModelList) {
                DistributeConfigResModel resModel = new DistributeConfigResModel();
                BeanUtils.copyProperties(req, resModel);
                while (true) {
                    if (baseCaseList.isEmpty()) {
                        //分配案件数量百分比
                        double perNum = new BigDecimal(resModel.getCaseNumTotal()).divide(new BigDecimal(caseNumTotal), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        resModel.setPerNum(perNum);
                        distributeResults.add(resModel);
                        break;
                    }
                    //取低位值
                    BaseCase low = baseCaseList.getFirst();
                    //分配案件数
                    resModel.setCaseNumTotal(resModel.getCaseNumTotal() + 1);
                    //分配金额数
                    resModel.setLeftAmtTotal(new BigDecimal(low.getLeftAmt() + resModel.getLeftAmtTotal()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                    resModel.getResultMap().put(low.getId(), low.getLeftAmt());
                    baseCaseList.remove(low);
                    //判断是否满足限制条件
                    if (resModel.getLeftAmtTotal() >= resModel.getLimitAmt() || resModel.getCaseNumTotal() >= resModel.getLimitNum()) {
                        double perNum = new BigDecimal(resModel.getCaseNumTotal()).divide(new BigDecimal(caseNumTotal), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        resModel.setPerNum(perNum);
                        distributeResults.add(resModel);
                        //取下一个催员
                        break;
                    } else {
                        //取高位
                        if (!baseCaseList.isEmpty()) {
                            BaseCase high = baseCaseList.getLast();
                            //分配数量
                            resModel.setCaseNumTotal(resModel.getCaseNumTotal() + 1);
                            //分配金额
                            resModel.setLeftAmtTotal(new BigDecimal(high.getLeftAmt() + resModel.getLeftAmtTotal()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                            resModel.getResultMap().put(high.getId(), high.getLeftAmt());
                            baseCaseList.remove(high);
                            //判断是否满足限制条件
                            if (resModel.getLeftAmtTotal() >= resModel.getLimitAmt() || resModel.getCaseNumTotal() >= resModel.getLimitNum()) {
                                double perNum = new BigDecimal(resModel.getCaseNumTotal()).divide(new BigDecimal(caseNumTotal), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                resModel.setPerNum(perNum);
                                distributeResults.add(resModel);
                                //取下一个催员
                                break;
                            }
                        }
                    }
                }
            }
            //处理剩余未分配的案子
            if (!baseCaseList.isEmpty()) {
                distributeLeafList(baseCaseList, distributeResults, caseNumTotal);
            }
        }
    }

    /**
     * 分配剩余未分配出去的案子(数量分配)
     *
     * @param leftBaseList
     * @param distributeResults
     */
    private void distributeLeafList(LinkedList<BaseCase> leftBaseList, List<DistributeConfigResModel> distributeResults, long caseNumTotal) {
        //将分配结果从小到大排序
        distributeResults.sort(Comparator.comparing(DistributeConfigResModel::getCaseNumTotal));
        //剩余案子大于分配人员时
        if (leftBaseList.size() > distributeResults.size()) {
            //计算剩余案件每个催员的平均值
            //平均数
            int pageSize = leftBaseList.size() / distributeResults.size();
            //总页数
            int copyCount = 0;
            if (leftBaseList.size() % pageSize > 0) {
                copyCount = leftBaseList.size() / pageSize + 1;
            } else {
                copyCount = leftBaseList.size() / pageSize;
            }
            for (int pageNo = 1; pageNo <= copyCount; pageNo++) {
                int startIndex = pageSize * (pageNo - 1);
                int endIndex = pageSize * pageNo;
                if (endIndex > leftBaseList.size()) {
                    endIndex = leftBaseList.size();
                }
                List<BaseCase> pageCaseList = leftBaseList.subList(startIndex, endIndex);
                if (pageNo <= distributeResults.size()) {
                    DistributeConfigResModel res = distributeResults.get(pageNo - 1);
                    for (BaseCase baseCase : pageCaseList) {
                        res.setCaseNumTotal(res.getCaseNumTotal() + 1);
                        res.setLeftAmtTotal(new BigDecimal(res.getLeftAmtTotal() + baseCase.getLeftAmt()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                        res.getResultMap().put(baseCase.getId(), baseCase.getLeftAmt());
                        double perNum = new BigDecimal(res.getCaseNumTotal()).divide(new BigDecimal(caseNumTotal), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        res.setPerNum(perNum);
                    }
                } else {
                    //最后一页
                    //将分配结果从小到大排序
                    distributeResults.sort(Comparator.comparing(DistributeConfigResModel::getCaseNumTotal));
                    for (int k = 0; k < distributeResults.size(); k++) {
                        DistributeConfigResModel res = distributeResults.get(k);
                        if (k < pageCaseList.size()) {
                            res.setCaseNumTotal(res.getCaseNumTotal() + 1);
                            res.setLeftAmtTotal(new BigDecimal(res.getLeftAmtTotal() + pageCaseList.get(k).getLeftAmt()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                            res.getResultMap().put(pageCaseList.get(k).getId(), pageCaseList.get(k).getLeftAmt());
                            double perNum = new BigDecimal(res.getCaseNumTotal()).divide(new BigDecimal(caseNumTotal), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
                            res.setPerNum(perNum);
                        } else {
                            break;
                        }
                    }
                }
            }
        } else {
            for (int k = 0; k < distributeResults.size(); k++) {
                DistributeConfigResModel res = distributeResults.get(k);
                if (k < leftBaseList.size()) {
                    res.setCaseNumTotal(res.getCaseNumTotal() + 1);
                    res.setLeftAmtTotal(new BigDecimal(res.getLeftAmtTotal() + leftBaseList.get(k).getLeftAmt()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                    res.getResultMap().put(leftBaseList.get(k).getId(), leftBaseList.get(k).getLeftAmt());
                    double perNum = new BigDecimal(res.getCaseNumTotal()).divide(new BigDecimal(caseNumTotal), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    res.setPerNum(perNum);
                } else {
                    break;
                }
            }
        }
    }

    /**
     * 外访执行案件分配(综合分配)
     */
    public void proAssistSysDistribute(List<String> assisiBaseCaseList, List<DistributeConfigResModel> distributeResults, long caseNumTotal) {
        if (assisiBaseCaseList.size() > distributeResults.size()) {
            //平均数
            int pageSize = assisiBaseCaseList.size() / distributeResults.size();
            //总页数
            int copyCount = 0;
            if (assisiBaseCaseList.size() % pageSize > 0) {
                copyCount = assisiBaseCaseList.size() / pageSize + 1;
            } else {
                copyCount = assisiBaseCaseList.size() / pageSize;
            }
            for (int pageNo = 1; pageNo <= copyCount; pageNo++) {
                int startIndex = pageSize * (pageNo - 1);
                int endIndex = pageSize * pageNo;
                if (endIndex > assisiBaseCaseList.size()) {
                    endIndex = assisiBaseCaseList.size();
                }
                List<String> pageCaseList = assisiBaseCaseList.subList(startIndex, endIndex);
                if (pageNo <= distributeResults.size()) {
                    DistributeConfigResModel res = distributeResults.get(pageNo - 1);
                    for (String id : pageCaseList) {
                        res.setLimitNum(Long.valueOf(pageSize));
                        res.setCaseNumTotal(res.getCaseNumTotal() + 1);
                        res.getResultMap().put(id, 0.0);
                        double perNum = new BigDecimal(res.getCaseNumTotal()).divide(new BigDecimal(caseNumTotal), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        res.setPerNum(perNum);
                    }
                } else {
                    //最后一页
                    //将分配结果从小到大排序
                    distributeResults.sort(Comparator.comparing(DistributeConfigResModel::getCaseNumTotal));
                    for (int k = 0; k < distributeResults.size(); k++) {
                        DistributeConfigResModel res = distributeResults.get(k);
                        if (k < pageCaseList.size()) {
                            res.setLimitNum(Long.valueOf(pageSize));
                            res.setCaseNumTotal(res.getCaseNumTotal() + 1);
                            res.getResultMap().put(assisiBaseCaseList.get(k), 0.0);
                            double perNum = new BigDecimal(res.getCaseNumTotal()).divide(new BigDecimal(caseNumTotal), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
                            res.setPerNum(perNum);
                        } else {
                            break;
                        }
                    }
                }
            }
        } else {
            for (int k = 0; k < distributeResults.size(); k++) {
                DistributeConfigResModel res = distributeResults.get(k);
                if (k < assisiBaseCaseList.size()) {
                    res.setLimitNum(0L);
                    res.setCaseNumTotal(res.getCaseNumTotal() + 1);
                    res.getResultMap().put(assisiBaseCaseList.get(k), 0.0);
                    double perNum = new BigDecimal(res.getCaseNumTotal()).divide(new BigDecimal(caseNumTotal), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    res.setPerNum(perNum);
                } else {
                    break;
                }
            }
        }
    }

    /**
     * 个人案件确认
     *
     * @param
     */
    public void confirmDistributeCase(List<DistributeConfigResModel> distributeConfigModelList,LoginResponse loginResponse,CaseIssuedFlag caseIssuedFlag ) {
        List<BaseCase> resultLists = new ArrayList<>();
        List<CaseTransferLog> caseTransferLogList=new ArrayList<>();
        for (DistributeConfigResModel obj : distributeConfigModelList) {
            Operator operator = new Operator();
            operator.setUsername(obj.getUserName());
            operator.setFullName(obj.getFullName());
            operator.setId(obj.getUserId());
            Map<String, Double> resultMap = obj.getResultMap();
            List<String> caseIds = new ArrayList<>();
            for (Map.Entry<String, Double> entry : resultMap.entrySet()) {
                caseIds.add(entry.getKey());
            }
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            queryBuilder.must(termsQuery("id.keyword", caseIds));
            Iterable<BaseCase> baseCaseIterable = importBaseCaseRepository.search(queryBuilder);
            List<BaseCase> baseCaseList = Lists.newArrayList(baseCaseIterable);
            for (BaseCase baseCase : baseCaseList) {
                baseCase.setDetaptId(obj.getDetaptId());
                baseCase.setDetaptName(obj.getDetaptName());
                baseCase.setIssuedFlag(caseIssuedFlag);
                baseCase.setOperator(obj.getUserName());
                baseCase.setOperatorTime(ZWDateUtil.getNowDateTime());
                baseCase.setCurrentCollector(operator);
                Set<String> departments = baseCase.getDepartments();
                if (Objects.isNull(departments)) {
                    departments = new HashSet<>();
                }
                departments.add(obj.getDetaptId());
                departments.add(loginResponse.getUser().getOrganization());
                baseCase.setDepartments(departments);
                baseCase.setFollowInTime(ZWDateUtil.getNowDateTime());
                caseTransferLogList.add(dataimpBaseService.createLog(baseCase.getId(),obj.getDetaptName().concat("->").
                                concat(baseCase.getCurrentCollector().getFullName()) ,loginResponse));
            }
            resultLists.addAll(baseCaseList);
        }
        if (!resultLists.isEmpty()) {
            importBaseCaseRepository.saveAll(resultLists);
        }
        if(!caseTransferLogList.isEmpty()){
            caseTransferLogRepository.saveAll(caseTransferLogList);
        }

    }

    /**
     * 区域案件确认
     *
     * @param
     */
    public void confirmAreaDistributeCase( List<DistributeConfigResModel> distributeConfigModelList,LoginResponse loginResponse,CaseIssuedFlag caseIssuedFlag ) {
        List<BaseCase> resultLists = new ArrayList<>();
        List<CaseTransferLog> caseTransferLogList=new ArrayList<>();
        for (DistributeConfigResModel obj : distributeConfigModelList) {
            Map<String, Double> resultMap = obj.getResultMap();
            Set<String> departmentIds = new HashSet<>();
            try {
                ResponseEntity<List<Organization>> responseEntity = organizationClient.findOrgIdsByLevelLess(obj.getDetaptId());
                List<Organization> all = responseEntity.getBody();
                for (Organization org : all) {
                    departmentIds.add(org.getId());
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new BadRequestException(null, "findOrgIdsByLevelLess", "findOrgIdsByLevelLess.is fail");
            }

            List<String> caseIds = new ArrayList<>();
            for (Map.Entry<String, Double> entry : resultMap.entrySet()) {
                caseIds.add(entry.getKey());
            }
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            queryBuilder.must(termsQuery("id.keyword", caseIds));
            Iterable<BaseCase> baseCaseIterable = importBaseCaseRepository.search(queryBuilder);
            List<BaseCase> baseCaseList = Lists.newArrayList(baseCaseIterable);
            for (BaseCase baseCase : baseCaseList) {
                baseCase.setDetaptId(obj.getDetaptId());
                baseCase.setDetaptName(obj.getDetaptName());
                baseCase.setOperator(obj.getUserName());
                baseCase.setOperatorTime(ZWDateUtil.getNowDateTime());
                baseCase.setIssuedFlag(caseIssuedFlag);
                Set<String> departments = baseCase.getDepartments();
                if (Objects.isNull(departments)) {
                    departments = new HashSet<>();
                }
                departments.add(obj.getDetaptId());
                if (!departmentIds.isEmpty()) {
                    departments.addAll(departmentIds);
                }
                departments.add(loginResponse.getUser().getOrganization());
                baseCase.setDepartments(departments);
                baseCase.setFollowInTime(ZWDateUtil.getNowDateTime());
                caseTransferLogList.add(dataimpBaseService.createLog(baseCase.getId(),"案件流转:".concat(obj.getDetaptName()),loginResponse));
            }
            resultLists.addAll(baseCaseList);
        }
        if (!resultLists.isEmpty()) {
            importBaseCaseRepository.saveAll(resultLists);
        }
        if(!caseTransferLogList.isEmpty()){
            caseTransferLogRepository.saveAll(caseTransferLogList);
        }

    }

    /**
     * 外访协助案件确认分配
     *
     * @param response
     */
    public void confirmAssistDistributeCase(GroupCaseDistributeResponse response) {
        List<DistributeConfigResModel> distributeConfigModelList = response.getDistributeConfigModels();
        List<AssistCollectionCase> resultLists = new ArrayList<>();
        for (DistributeConfigResModel obj : distributeConfigModelList) {
            Operator operator = new Operator();
            operator.setUsername(obj.getUserName());
            operator.setFullName(obj.getFullName());
            operator.setId(obj.getUserId());
            operator.setOrganization(obj.getDetaptId());
            Map<String, Double> resultMap = obj.getResultMap();
            List<String> caseIds = new ArrayList<>();
            for (Map.Entry<String, Double> entry : resultMap.entrySet()) {
                caseIds.add(entry.getKey());
            }
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            queryBuilder.must(termsQuery("id.keyword", caseIds));
            Iterable<AssistCollectionCase> baseCaseIterable = impAssistCaseRepository.search(queryBuilder);
            List<AssistCollectionCase> baseCaseList = Lists.newArrayList(baseCaseIterable);
            for (AssistCollectionCase assistCollectionCase : baseCaseList) {
                assistCollectionCase.setOperator(obj.getUserName());
                assistCollectionCase.setOperatorTime(ZWDateUtil.getNowDateTime());
                assistCollectionCase.setCurrentCollector(operator);
                assistCollectionCase.setAssistStatus(AssistStatus.ASSIST_COLLECTING);
                Set<String> departments = assistCollectionCase.getDepartments();
                if (Objects.isNull(departments)) {
                    departments = new HashSet<>();
                }
                departments.add(obj.getDetaptId());
                assistCollectionCase.setDepartments(departments);
            }
            resultLists.addAll(baseCaseList);
        }
        impAssistCaseRepository.saveAll(resultLists);
    }


}

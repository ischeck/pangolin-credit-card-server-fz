package cn.fintecher.pangolin.service.dataimp.service;

import cn.fintecher.pangolin.common.annotation.ExcelAnno;
import cn.fintecher.pangolin.common.enums.*;
import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.*;
import cn.fintecher.pangolin.common.utils.*;
import cn.fintecher.pangolin.entity.domain.*;
import cn.fintecher.pangolin.entity.managentment.*;
import cn.fintecher.pangolin.service.dataimp.model.ExportCaseModel;
import cn.fintecher.pangolin.service.dataimp.model.request.AssistManagementImportRequest;
import cn.fintecher.pangolin.service.dataimp.model.request.ExportAssistManagementRequest;
import cn.fintecher.pangolin.service.dataimp.model.request.ExportSearchRequest;
import cn.fintecher.pangolin.common.model.CaseInfoPropertyResponse;
import cn.fintecher.pangolin.service.dataimp.model.response.ExportFollowRecordModel;
import cn.fintecher.pangolin.service.dataimp.repository.*;
import cn.fintecher.pangolin.service.dataimp.web.ImportExcelCaseController;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.io.FileUtils;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static cn.fintecher.pangolin.common.utils.InnerServiceUrl.MANAGEMENT_SERVICE_PRIMARY_EXPORTCONFIG;
import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;


/**
 * @Author:peishouwen
 * @Desc: 协催管理导出
 * @Date:Create in 13:43 2018/8/31
 */
@Service("exportAssistManagementService")
public class ExportAssistManagementService {

    private final Logger logger = LoggerFactory.getLogger(ImportExcelCaseController.class);

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    BaseCaseImportExcelTempService caseImportExcelTempService;

    @Autowired
    ImpPayAmtLogRepository impPayAmtLogRepository;

    @Autowired
    BasicCaseApplyRepository basicCaseApplyRepository;

    @Autowired
    CaseFollowupRecordRepository caseFollowupRecordRepository;

    @Autowired
    private BaseCaseRepository baseCaseRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private DataimpBaseService dataimpBaseService;

    @Autowired
    ImportHisCaseRepository importHisCaseRepository;

    @Autowired
    PersonalContactImpRepository personalContactImpRepository;

    @Autowired
    ModelMapper modelMapper;

    /**
     * 获取导出表头
     *
     * @return
     * @throws Exception
     */
    @Async
    public void getExportTitle(ExportAssistManagementRequest request, String token) throws Exception {

        OperatorModel userByToken = dataimpBaseService.getUserByToken(token);
        String fileUrl = null;
        UploadFile uploadFile = null;
        TaskBoxModel taskBoxModel = new TaskBoxModel();
        Snowflake snowflake = new Snowflake((int) (Thread.currentThread().getId() % 1024));
        taskBoxModel.setId(String.valueOf(snowflake.next()));
        dataimpBaseService.sendTaskBoxMessage(taskBoxModel, TaskBoxType.EXPORT, TaskBoxStatus.UN_FINISH, null, null, userByToken, ZWDateUtil.getNowDateTime(), null, "申请类信息导出");
        List<Class<?>> objClassList = new ArrayList<>();
        List<DerateAmountModel> dataList;
        List<CheckAccountModel> checkAccountModels;
        String fieldName = "";
        try {
            List<BasicCaseApply> basicCaseApplyList = getBatchBasicApply(request);
            switch (request.getApplyType()) {
                case DERATE_APPLY:
                    objClassList.add(DerateAmountModel.class);
                    fieldName = fieldName.concat("减免申请导出_").concat(String.valueOf(System.currentTimeMillis())).concat(".xlsx");
                    dataList = getDerateAmountData(basicCaseApplyList);
                    uploadFile = exportData(taskBoxModel, userByToken, objClassList, dataList, fieldName, fileUrl);
                    break;
                case CHECK_OVERDUE_AMOUNT_APPLY:
                    fieldName = fieldName.concat("查账申请导出_").concat(String.valueOf(System.currentTimeMillis())).concat(".xlsx");
                    objClassList.add(CheckAccountModel.class);
                    checkAccountModels = getCheckAccountData(basicCaseApplyList);
                    uploadFile = exportData(taskBoxModel, userByToken, objClassList, checkAccountModels, fieldName, fileUrl);
                    break;
                case SUPPLEMENT_APPLY:
                    fieldName = fieldName.concat("补款申请导出_").concat(String.valueOf(System.currentTimeMillis())).concat(".xlsx");
                    objClassList.add(SupplementAmountModel.class);
                    List<SupplementAmountModel> supplementAmountData = getSupplementAmountData(basicCaseApplyList);
                    uploadFile = exportData(taskBoxModel, userByToken, objClassList, supplementAmountData, fieldName, fileUrl);
                    break;
                case REPORT_CASE_APPLY:
                    fieldName = fieldName.concat("报案申请导出_").concat(String.valueOf(System.currentTimeMillis())).concat(".xlsx");
                    objClassList.add(ReportCaseModel.class);
                    List<ReportCaseModel> reportCaseData = getReportCaseData(basicCaseApplyList);
                    uploadFile = exportData(taskBoxModel, userByToken, objClassList, reportCaseData, fieldName, fileUrl);
                    break;
                case CHECK_MATERIAL_APPLY:
                    fieldName = fieldName.concat("申请材料导出_").concat(String.valueOf(System.currentTimeMillis())).concat(".xlsx");
                    objClassList.add(ApplyMaterialModel.class);
                    List<ApplyMaterialModel> checkMaterialData = getCheckMaterialData(basicCaseApplyList);
                    uploadFile = exportData(taskBoxModel, userByToken, objClassList, checkMaterialData, fieldName, fileUrl);
                    break;
                case LEAVE_CASE_APPLY:
                    fieldName = fieldName.concat("留案导出_").concat(String.valueOf(System.currentTimeMillis())).concat(".xlsx");
                    objClassList.add(LeaveCaseModel.class);
                    List<LeaveCaseModel> leaveCaseData = getLeaveCaseData(basicCaseApplyList);
                    uploadFile = exportData(taskBoxModel, userByToken, objClassList, leaveCaseData, fieldName, fileUrl);
                    break;
                default:
                    throw new BadRequestException(null, "apply", "apply.is.not.exist");
            }
        } catch (Exception e) {
            e.printStackTrace();
            dataimpBaseService.sendTaskBoxMessage(taskBoxModel, TaskBoxType.EXPORT, TaskBoxStatus.FAILURE, uploadFile.getId(), uploadFile.getOriginalName(), userByToken, null, ZWDateUtil.getNowDateTime(), "申请类信息导出");
        }
        dataimpBaseService.sendTaskBoxMessage(taskBoxModel, TaskBoxType.EXPORT, TaskBoxStatus.FINISHED, uploadFile.getId(), uploadFile.getOriginalName(), userByToken, null, ZWDateUtil.getNowDateTime(), "申请类信息导出");
    }

    /**
     * 导出数据
     *
     * @param objClassList
     * @param dataList
     * @param fileName
     */
    private UploadFile exportData(TaskBoxModel taskBoxModel, OperatorModel userByToken, List<Class<?>> objClassList, List<?> dataList, String fileName, String fileUrl) {
        List<String> list = new ArrayList<>();
        List<String> properties = new ArrayList<>();
        String filePath = FileUtils.getTempDirectoryPath();
        filePath = filePath.concat(fileName);
        UploadFile file = null;
        List<CaseInfoPropertyResponse> responses = caseImportExcelTempService.getObjejctPro(objClassList);
        responses.forEach(response -> {
            list.add(response.getName());
            properties.add(response.getAttribute());
        });
        String[] title = new String[list.size()];
        list.toArray(title);
        String[] property = new String[list.size()];
        properties.toArray(property);
        try {
            ExportDataToExcelUtil.exportToExcel(dataList, title, property, filePath);
            file = dataimpBaseService.getFileUrl(filePath);
        } catch (Exception e) {
            e.printStackTrace();
            dataimpBaseService.sendTaskBoxMessage(taskBoxModel, TaskBoxType.EXPORT, TaskBoxStatus.FAILURE, file.getId(), file.getOriginalName(), userByToken, null, ZWDateUtil.getNowDateTime(), "申请类信息导出");
        }
        return file;
    }

    /**
     * 查询需要导出各个申请的记录
     *
     * @param exportAssistManagementRequest
     * @return
     */
    private List<BasicCaseApply> getBatchBasicApply(ExportAssistManagementRequest exportAssistManagementRequest) {
        List<BasicCaseApply> list = new ArrayList<>();
        Iterable<BasicCaseApply> search = basicCaseApplyRepository.search(exportAssistManagementRequest.generateQueryBuilder());
        if (search.iterator().hasNext()) {
            list = IteratorUtils.toList(search.iterator());
        }
        return list;
    }

    /**
     * 获取对账单还款金额
     *
     * @param listId
     * @return
     */
    private Map<String, Double> getPayAmtLog(List<String> listId) {

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(termsQuery("id.keyword", listId));
        //根据不同的案件对还款金额进行聚合统计
        TermsAggregationBuilder field = AggregationBuilders.terms("countId").field("id.keyword");
        SumAggregationBuilder sumBuilder = AggregationBuilders.sum("sum").field("latestPayAmt");
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices("pay_amt_log")
                .withTypes("pay_amt_log")
                .withSearchType(SearchType.DEFAULT)
                .withQuery(queryBuilder)
                .addAggregation(field.subAggregation(sumBuilder)).build();
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, response -> response.getAggregations());
        Map<String, Double> buffList = new HashMap();
        Map<String, Aggregation> map = aggregations.asMap();
        if (map.get("countId") instanceof StringTerms) {
            StringTerms count = (StringTerms) map.get("countId");
            for (StringTerms.Bucket bucket : count.getBuckets()) {
                InternalSum sum = bucket.getAggregations().get("sum");
                buffList.put(bucket.getKeyAsString(), sum.getValue());
            }
        }
        return buffList;
    }

    /**
     * 获取协助管理
     *
     * @param basicCaseApply
     * @return
     */
    private AssistManagementModel setAssistManagement(BasicCaseApply basicCaseApply) {
        AssistManagementModel model = new AssistManagementModel();
        BeanUtils.copyProperties(basicCaseApply, model);
        if (basicCaseApply.getCardInformationSet().size() > 0) {
            basicCaseApply.getCardInformationSet().forEach(cardInformation -> {
                if (Objects.nonNull(cardInformation.getCardNo())) {
                    model.setCardNo(("/").concat(cardInformation.getCardNo()));
                    model.setCardNo(model.getCardNo().substring(1));
                }
            });
        }
        return model;
    }

    /***
     * 减免申请导出
     * @param basicCaseApplyList
     * @return
     */
    private List<DerateAmountModel> getDerateAmountData(List<BasicCaseApply> basicCaseApplyList) {
        List<DerateAmountModel> models = new ArrayList<>();
        for (BasicCaseApply basicCaseApply : basicCaseApplyList) {
            DerateAmountModel model = new DerateAmountModel();
            BeanUtils.copyProperties(basicCaseApply, model);
            AssistManagementModel managementModel = setAssistManagement(basicCaseApply);
            BeanUtils.copyProperties(managementModel, model);
            models.add(model);
            basicCaseApply.setExportState(ExportState.EXPORTED);
        }
        if (basicCaseApplyList.size() > 0) {
            basicCaseApplyRepository.saveAll(basicCaseApplyList);
        }
        return models;
    }

    /***
     * 查账申请导出
     * @param basicCaseApplyList
     * @return
     */
    private List<CheckAccountModel> getCheckAccountData(List<BasicCaseApply> basicCaseApplyList) {
        List<CheckAccountModel> models = new ArrayList<>();
        for (BasicCaseApply basicCaseApply : basicCaseApplyList) {
            CheckAccountModel model = new CheckAccountModel();
            BeanUtils.copyProperties(basicCaseApply, model);
            AssistManagementModel managementModel = setAssistManagement(basicCaseApply);
            BeanUtils.copyProperties(managementModel, model);
            models.add(model);
            basicCaseApply.setExportState(ExportState.EXPORTED);
        }
        if (basicCaseApplyList.size() > 0) {
            basicCaseApplyRepository.saveAll(basicCaseApplyList);
        }
        return models;
    }

    /***
     * 报案申请导出
     * @param basicCaseApplyList
     * @return
     */
    private List<ReportCaseModel> getReportCaseData(List<BasicCaseApply> basicCaseApplyList) {
        List<ReportCaseModel> models = new ArrayList<>();
        for (BasicCaseApply basicCaseApply : basicCaseApplyList) {
            ReportCaseModel model = new ReportCaseModel();
            BeanUtils.copyProperties(basicCaseApply, model);
            AssistManagementModel managementModel = setAssistManagement(basicCaseApply);
            BeanUtils.copyProperties(managementModel, model);
            models.add(model);
            basicCaseApply.setExportState(ExportState.EXPORTED);
        }
        if (basicCaseApplyList.size() > 0) {
            basicCaseApplyRepository.saveAll(basicCaseApplyList);
        }
        return models;
    }

    /***
     * 申请材料导出
     * @param basicCaseApplyList
     * @return
     */
    private List<ApplyMaterialModel> getCheckMaterialData(List<BasicCaseApply> basicCaseApplyList) {
        List<ApplyMaterialModel> models = new ArrayList<>();
        for (BasicCaseApply basicCaseApply : basicCaseApplyList) {
            ApplyMaterialModel model = new ApplyMaterialModel();
            BeanUtils.copyProperties(basicCaseApply, model);
            model.setApplyContent(basicCaseApply.getApplyContent().name());
            models.add(model);
            basicCaseApply.setExportState(ExportState.EXPORTED);
        }
        if (basicCaseApplyList.size() > 0) {
            basicCaseApplyRepository.saveAll(basicCaseApplyList);
        }
        return models;
    }

    /***
     * 留案申请导出
     * @param basicCaseApplyList
     * @return
     */
    private List<LeaveCaseModel> getLeaveCaseData(List<BasicCaseApply> basicCaseApplyList) {
        List<LeaveCaseModel> models = new ArrayList<>();
        for (BasicCaseApply basicCaseApply : basicCaseApplyList) {
            LeaveCaseModel model = new LeaveCaseModel();
            BeanUtils.copyProperties(basicCaseApply, model);
            AssistManagementModel managementModel = setAssistManagement(basicCaseApply);
            BeanUtils.copyProperties(managementModel, model);
            models.add(model);
            basicCaseApply.setExportState(ExportState.EXPORTED);
        }
        if (basicCaseApplyList.size() > 0) {
            basicCaseApplyRepository.saveAll(basicCaseApplyList);
        }
        return models;
    }

    /***
     * 补款申请导出
     * @param basicCaseApplyList
     * @return
     */
    private List<SupplementAmountModel> getSupplementAmountData(List<BasicCaseApply> basicCaseApplyList) {
        List<SupplementAmountModel> models = new ArrayList<>();
        List<String> list = new ArrayList<>();
        basicCaseApplyList.forEach(basicCaseApply -> list.add(basicCaseApply.getCaseId()));
        Map<String, Double> paymentMap = getPayAmtLog(list);
        for (BasicCaseApply basicCaseApply : basicCaseApplyList) {
            SupplementAmountModel model = new SupplementAmountModel();
            BeanUtils.copyProperties(basicCaseApply, model);
            AssistManagementModel managementModel = setAssistManagement(basicCaseApply);
            BeanUtils.copyProperties(managementModel, model);
            if (paymentMap.containsKey(basicCaseApply.getCaseId())) {
                model.setTotalPaymentAmount(paymentMap.get(basicCaseApply.getCaseId()));
            }
            models.add(model);
            basicCaseApply.setExportState(ExportState.EXPORTED);
        }
        if (basicCaseApplyList.size() > 0) {
            basicCaseApplyRepository.saveAll(basicCaseApplyList);
        }
        return models;
    }


    /**
     * 导出在案催记
     *
     * @param configId
     */
    public void exportInPoolCaseFollowRecord(String configId, ExportSearchRequest request, OperatorModel operator) {
        TaskBoxModel taskBoxModel = new TaskBoxModel();
        Snowflake snowflake = new Snowflake((int) (Thread.currentThread().getId() % 1024));
        taskBoxModel.setId(String.valueOf(snowflake.next()));
        dataimpBaseService.sendTaskBoxMessage(taskBoxModel, TaskBoxType.EXPORT, TaskBoxStatus.UN_FINISH, null, null, operator, ZWDateUtil.getNowDateTime(), null, "催收记录导出");
        new Thread(() -> {
            try {
                List<Object> models = new ArrayList<>();
                List<String> titleNames = new ArrayList<>();
                List<String> dataPros = new ArrayList<>();
                ExportConfig exportConfig = restTemplate.getForEntity(MANAGEMENT_SERVICE_PRIMARY_EXPORTCONFIG + configId, ExportConfig.class).getBody();
                for (ExportConfigItem item : exportConfig.getItems()) {
                    if (ZWStringUtils.isNotEmpty(item.getTitleName())) {
                        titleNames.add(item.getTitleName());
                        dataPros.add(item.getAttribute());
                    }
                }
                List<BaseCase> baseCases = IterableUtils.toList(baseCaseRepository.search(request.generateQueryBuilder()));
                if(baseCases.size() > 10000){
                    dataimpBaseService.sendTaskBoxMessage(taskBoxModel, TaskBoxType.EXPORT, TaskBoxStatus.FAILURE, null, null, operator, null, ZWDateUtil.getNowDateTime(), "催收记录导出失败，案件数量大于10000");
                }
                for (BaseCase baseCase : baseCases) {
                    List<CaseFollowupRecord> caseFollowupRecords = IterableUtils.toList(caseFollowupRecordRepository.search(QueryBuilders.boolQuery().must(matchPhraseQuery("caseId", baseCase.getId()))));
                    for (CaseFollowupRecord record : caseFollowupRecords) {
                        ExportFollowRecordModel model = new ExportFollowRecordModel();
                        BeanUtils.copyProperties(record, model);
                        BeanUtils.copyPropertiesIgnoreNull(baseCase, model);
                        BeanUtils.copyPropertiesIgnoreNull(baseCase.getPersonal(), model);
                        models.add(model);
                    }
                }
                String path = FileUtils.getTempDirectoryPath().concat("跟进记录" + System.currentTimeMillis()).concat(".xlsx");
                ExportDataToExcelUtil.exportToExcel(models, titleNames.toArray(new String[titleNames.size()]), dataPros.toArray(new String[dataPros.size()]), path);
                UploadFile uploadFile = dataimpBaseService.getFileUrl(path);
                dataimpBaseService.sendTaskBoxMessage(taskBoxModel, TaskBoxType.EXPORT, TaskBoxStatus.FINISHED, uploadFile.getId(), uploadFile.getOriginalName(), operator, null, ZWDateUtil.getNowDateTime(), "催收记录导出");
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                dataimpBaseService.sendTaskBoxMessage(taskBoxModel, TaskBoxType.EXPORT, TaskBoxStatus.FAILURE, null, null, operator, null, ZWDateUtil.getNowDateTime(), "催收记录导出");
            }
        }).start();
    }

    /**
     * 导出历史案件催记
     *
     * @param configId
     */
    public void exportHisCaseFollowRecord(String configId, ExportSearchRequest request, OperatorModel operator) {
        TaskBoxModel taskBoxModel = new TaskBoxModel();
        Snowflake snowflake = new Snowflake((int) (Thread.currentThread().getId() % 1024));
        taskBoxModel.setId(String.valueOf(snowflake.next()));
        dataimpBaseService.sendTaskBoxMessage(taskBoxModel, TaskBoxType.EXPORT, TaskBoxStatus.UN_FINISH, null, null, operator, ZWDateUtil.getNowDateTime(), null, "催收记录导出");
        new Thread(() -> {
            try {
                List<Object> models = new ArrayList<>();
                List<String> titleNames = new ArrayList<>();
                List<String> dataPros = new ArrayList<>();
                ExportConfig exportConfig = restTemplate.getForEntity(MANAGEMENT_SERVICE_PRIMARY_EXPORTCONFIG + configId, ExportConfig.class).getBody();
                for (ExportConfigItem item : exportConfig.getItems()) {
                    if (ZWStringUtils.isNotEmpty(item.getTitleName())) {
                        titleNames.add(item.getTitleName());
                        dataPros.add(item.getAttribute());
                    }
                }
                List<HisCase> hisCases = IterableUtils.toList(importHisCaseRepository.search(request.generateQueryBuilder()));
                if(hisCases.size() > 10000){
                    dataimpBaseService.sendTaskBoxMessage(taskBoxModel, TaskBoxType.EXPORT, TaskBoxStatus.FAILURE, null, null, operator, null, ZWDateUtil.getNowDateTime(), "催收记录导出失败，案件数量大于10000");
                }
                for (HisCase hisCase : hisCases) {
                    List<CaseFollowupRecord> caseFollowupRecords = IterableUtils.toList(caseFollowupRecordRepository.search(QueryBuilders.boolQuery().must(matchPhraseQuery("caseId", hisCase.getId()))));
                    for (CaseFollowupRecord record : caseFollowupRecords) {
                        ExportFollowRecordModel model = new ExportFollowRecordModel();
                        BeanUtils.copyProperties(record, model);
                        BeanUtils.copyPropertiesIgnoreNull(hisCase, model);
                        BeanUtils.copyPropertiesIgnoreNull(hisCase.getPersonal(), model);
                        models.add(model);
                    }
                }
                String path = FileUtils.getTempDirectoryPath().concat("跟进记录" + System.currentTimeMillis()).concat(".xlsx");
                ExportDataToExcelUtil.exportToExcel(models, titleNames.toArray(new String[titleNames.size()]), dataPros.toArray(new String[dataPros.size()]), path);
                UploadFile uploadFile = dataimpBaseService.getFileUrl(path);
                dataimpBaseService.sendTaskBoxMessage(taskBoxModel, TaskBoxType.EXPORT, TaskBoxStatus.FINISHED, uploadFile.getId(), uploadFile.getOriginalName(), operator, null, ZWDateUtil.getNowDateTime(), "催收记录导出");
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                dataimpBaseService.sendTaskBoxMessage(taskBoxModel, TaskBoxType.EXPORT, TaskBoxStatus.FAILURE, null, null, operator, null, ZWDateUtil.getNowDateTime(), "催收记录导出");
            }
        }).start();
    }


    /**
     * 导出在案案件
     *
     * @param configId
     */
    public void exportInPoolCase(String configId, ExportSearchRequest request, OperatorModel operator) {
        TaskBoxModel taskBoxModel = new TaskBoxModel();
        Snowflake snowflake = new Snowflake((int) (Thread.currentThread().getId() % 1024));
        taskBoxModel.setId(String.valueOf(snowflake.next()));
        dataimpBaseService.sendTaskBoxMessage(taskBoxModel, TaskBoxType.EXPORT, TaskBoxStatus.UN_FINISH, null, null, operator, ZWDateUtil.getNowDateTime(), null, "案件导出");
        new Thread(() -> {
            try {
                List<Object> models = new ArrayList<>();
                List<String> titleNames = new ArrayList<>();
                List<String> dataPros = new ArrayList<>();
                ExportConfig exportConfig = restTemplate.getForEntity(MANAGEMENT_SERVICE_PRIMARY_EXPORTCONFIG + configId, ExportConfig.class).getBody();
                for (ExportConfigItem item : exportConfig.getItems()) {
                    if (ZWStringUtils.isNotEmpty(item.getTitleName())) {
                        titleNames.add(item.getTitleName());
                        dataPros.add(item.getAttribute());
                    }
                }
                List<BaseCase> baseCases = IterableUtils.toList(baseCaseRepository.search(request.generateQueryBuilder()));
                if(baseCases.size() > 10000){
                    dataimpBaseService.sendTaskBoxMessage(taskBoxModel, TaskBoxType.EXPORT, TaskBoxStatus.FAILURE, null, null, operator, null, ZWDateUtil.getNowDateTime(), "催收记录导出失败，案件数量大于10000");
                }
                for (BaseCase baseCase : baseCases) {
                    Personal personal = baseCase.getPersonal();
                    Principal principal = baseCase.getPrincipal();
                    ExportCaseModel model = new ExportCaseModel();
                    BeanUtils.copyProperties(baseCase, model);
                    BeanUtils.copyPropertiesIgnoreNull(personal, model);
                    BeanUtils.copyPropertiesIgnoreNull(principal, model);
                    List<PersonalContact> personalContacts = IterableUtils.toList(personalContactImpRepository.search(QueryBuilders.boolQuery().must(matchPhraseQuery("personalId", personal.getId()))));
                    parsePersonalContact(model, personalContacts);
                    models.addAll(parseCardInfo(model, new ArrayList<>(baseCase.getCardInformationSet())));
                }
                String path = FileUtils.getTempDirectoryPath().concat("案件信息" + System.currentTimeMillis()).concat(".xlsx");
                ExportDataToExcelUtil.exportToExcel(models, titleNames.toArray(new String[titleNames.size()]), dataPros.toArray(new String[dataPros.size()]), path);
                UploadFile uploadFile = dataimpBaseService.getFileUrl(path);
                dataimpBaseService.sendTaskBoxMessage(taskBoxModel, TaskBoxType.EXPORT, TaskBoxStatus.FINISHED, uploadFile.getId(), uploadFile.getOriginalName(), operator, null, ZWDateUtil.getNowDateTime(), "案件导出");
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                dataimpBaseService.sendTaskBoxMessage(taskBoxModel, TaskBoxType.EXPORT, TaskBoxStatus.FAILURE, null, null, operator, null, ZWDateUtil.getNowDateTime(), "案件导出");
            }
        }).start();
    }

    /**
     * 导出历史案件
     *
     * @param configId
     */
    public void exportHisCase(String configId, ExportSearchRequest request, OperatorModel operator) {
        TaskBoxModel taskBoxModel = new TaskBoxModel();
        Snowflake snowflake = new Snowflake((int) (Thread.currentThread().getId() % 1024));
        taskBoxModel.setId(String.valueOf(snowflake.next()));
        dataimpBaseService.sendTaskBoxMessage(taskBoxModel, TaskBoxType.EXPORT, TaskBoxStatus.UN_FINISH, null, null, operator, ZWDateUtil.getNowDateTime(), null, "案件导出");
        new Thread(() -> {
            try {
                List<Object> models = new ArrayList<>();
                List<String> titleNames = new ArrayList<>();
                List<String> dataPros = new ArrayList<>();
                ExportConfig exportConfig = restTemplate.getForEntity(MANAGEMENT_SERVICE_PRIMARY_EXPORTCONFIG + configId, ExportConfig.class).getBody();
                for (ExportConfigItem item : exportConfig.getItems()) {
                    if (ZWStringUtils.isNotEmpty(item.getTitleName())) {
                        titleNames.add(item.getTitleName());
                        dataPros.add(item.getAttribute());
                    }
                }
                List<HisCase> hisCases = IterableUtils.toList(importHisCaseRepository.search(request.generateQueryBuilder()));
                if(hisCases.size() > 10000){
                    dataimpBaseService.sendTaskBoxMessage(taskBoxModel, TaskBoxType.EXPORT, TaskBoxStatus.FAILURE, null, null, operator, null, ZWDateUtil.getNowDateTime(), "催收记录导出失败，案件数量大于10000");
                }
                for (HisCase hisCase : hisCases) {
                    Personal personal = hisCase.getPersonal();
                    Principal principal = hisCase.getPrincipal();
                    ExportCaseModel model = new ExportCaseModel();
                    BeanUtils.copyProperties(hisCase, model);
                    BeanUtils.copyPropertiesIgnoreNull(personal, model);
                    BeanUtils.copyPropertiesIgnoreNull(principal, model);
                    List<PersonalContact> personalContacts = IterableUtils.toList(personalContactImpRepository.search(QueryBuilders.boolQuery().must(matchPhraseQuery("personalId", personal.getId()))));
                    parsePersonalContact(model, personalContacts);
                    models.addAll(parseCardInfo(model, new ArrayList<>(hisCase.getCardInformationSet())));
                }
                String path = FileUtils.getTempDirectoryPath().concat("案件信息" + System.currentTimeMillis()).concat(".xlsx");
                ExportDataToExcelUtil.exportToExcel(models, titleNames.toArray(new String[titleNames.size()]), dataPros.toArray(new String[dataPros.size()]), path);
                UploadFile uploadFile = dataimpBaseService.getFileUrl(path);
                dataimpBaseService.sendTaskBoxMessage(taskBoxModel, TaskBoxType.EXPORT, TaskBoxStatus.FINISHED, uploadFile.getId(), uploadFile.getOriginalName(), operator, null, ZWDateUtil.getNowDateTime(), "案件导出");
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                dataimpBaseService.sendTaskBoxMessage(taskBoxModel, TaskBoxType.EXPORT, TaskBoxStatus.FAILURE, null, null, operator, null, ZWDateUtil.getNowDateTime(), "案件导出");
            }
        }).start();
    }

    private void parsePersonalContact(ExportCaseModel model, List<PersonalContact> personalContacts) {
        for (int i = 0; i < personalContacts.size(); i++) {
            PersonalContact personalContact = personalContacts.get(i);
            if (i == 0) {
                model.setName1(personalContact.getName());
                model.setRelation1(personalContact.getRelation());
                model.setCertificateNo1(personalContact.getCertificateNo());
                model.setEmployerName1(personalContact.getEmployerName());
                model.setPersonalPerCall1(personalContact.getPersonalPerCalls().toString());
                List<String> phones = personalContact.getPersonalPerCalls().stream().map(PersonalPerCall::getPhoneNo).collect(Collectors.toList());
                model.setPersonalPerCall1(phones.toString());
            } else if (i == 1) {
                model.setName2(personalContact.getName());
                model.setRelation2(personalContact.getRelation());
                model.setCertificateNo2(personalContact.getCertificateNo());
                model.setEmployerName2(personalContact.getEmployerName());
                List<String> phones = personalContact.getPersonalPerCalls().stream().map(PersonalPerCall::getPhoneNo).collect(Collectors.toList());
                model.setPersonalPerCall1(phones.toString());
            } else if (i == 2) {
                model.setName3(personalContact.getName());
                model.setRelation3(personalContact.getRelation());
                model.setCertificateNo3(personalContact.getCertificateNo());
                model.setEmployerName3(personalContact.getEmployerName());
                List<String> phones = personalContact.getPersonalPerCalls().stream().map(PersonalPerCall::getPhoneNo).collect(Collectors.toList());
                model.setPersonalPerCall1(phones.toString());
            } else if (i == 3) {
                model.setName4(personalContact.getName());
                model.setRelation4(personalContact.getRelation());
                model.setCertificateNo4(personalContact.getCertificateNo());
                model.setEmployerName4(personalContact.getEmployerName());
                List<String> phones = personalContact.getPersonalPerCalls().stream().map(PersonalPerCall::getPhoneNo).collect(Collectors.toList());
                model.setPersonalPerCall1(phones.toString());
            } else if (i == 4) {
                model.setName5(personalContact.getName());
                model.setRelation5(personalContact.getRelation());
                model.setCertificateNo5(personalContact.getCertificateNo());
                model.setEmployerName5(personalContact.getEmployerName());
                List<String> phones = personalContact.getPersonalPerCalls().stream().map(PersonalPerCall::getPhoneNo).collect(Collectors.toList());
                model.setPersonalPerCall1(phones.toString());
            }
        }
    }

    private List<ExportCaseModel> parseCardInfo(ExportCaseModel model, List<CardInformation> cardInformations) {
        List<ExportCaseModel> models = new ArrayList<>();
        if (Objects.nonNull(cardInformations) && cardInformations.size() > 0) {
            for (int i = 0; i < cardInformations.size(); i++) {
                CardInformation cardInformation = cardInformations.get(i);
                ExportCaseModel temp = new ExportCaseModel();
                BeanUtils.copyProperties(model, temp);
                BeanUtils.copyPropertiesIgnoreNull(cardInformation,temp);
                models.add(temp);
            }
        }else{
            models.add(model);
        }
        return models;
    }

    /**
     * 获取指定类的属性
     *
     * @param objClassList
     * @return
     */
    public List<ExportConfigItem> getObjejctPro(List<Class<?>> objClassList) {
        List<ExportConfigItem> exportConfigItems = new ArrayList<>();
        for (Class<?> objClass : objClassList) {
            //获取类中所有的字段
            Field[] fields = objClass.getDeclaredFields();
            for (Field field : fields) {
                //获取标记了ExcelAnno的注解字段
                if (field.isAnnotationPresent(ExcelAnno.class)) {
                    ExcelAnno f = field.getAnnotation(ExcelAnno.class);
                    ExportConfigItem exportConfigItem = new ExportConfigItem();
                    exportConfigItem.setAttribute(field.getName());
                    exportConfigItem.setName(f.cellName());
                    exportConfigItem.setSource(f.fieldType().name());
                    exportConfigItems.add(exportConfigItem);
                }
            }
        }
        return exportConfigItems;
    }

    /***
     * 根据不同类型处理导入申请的数据
     * @param request
     * @param inputStream
     */
    @Async
    public void importExcelAssistManagementData(AssistManagementImportRequest request, InputStream inputStream, String token) {
        logger.info("协催管理(" + request.getApplyType() + ")导入开始.......");
        //获取用户信息
        OperatorModel operator = dataimpBaseService.getUserByToken(token);
        List<Class<?>> objClassList = new ArrayList<>();
        switch (request.getApplyType()) {
            case DERATE_APPLY:
                objClassList.add(DerateAmountModel.class);
                setDerateAmountApply(objClassList, inputStream, operator);
                break;
            case CHECK_OVERDUE_AMOUNT_APPLY:
                objClassList.add(CheckAccountModel.class);
                setcheckAccountApply(objClassList, inputStream, operator);
                break;
            case REPORT_CASE_APPLY:
                objClassList.add(ReportCaseModel.class);
                setReportCaseApply(objClassList, inputStream, operator);
                break;
            case LEAVE_CASE_APPLY:
                objClassList.add(LeaveCaseModel.class);
                setLeaveCaseApply(objClassList, inputStream, operator);
                break;
            default:
                break;
        }

    }

    /***
     * 改变导入减免申请案件状态
     * @param objClassList
     * @param inputStream
     */
    private void setDerateAmountApply(List<Class<?>> objClassList, InputStream inputStream, OperatorModel operatorModel) {
        List<Map<String, String>> sheetDataList = parseExcelAssistManagementData(inputStream);
        Map<String, ImportExcelConfigItem> headMap = excelConfigItemToMap(sheetDataList, inputStream, objClassList);
        sheetDataList.remove(sheetDataList.get(0));
        List<String> errorList = new ArrayList<>();
        List<String> idList = new ArrayList<>();
        Map<String, DerateAmountModel> mapDerateAmoubt = new HashMap<>();
        Long rowIndex = 1L;
        for (Map<String, String> map : sheetDataList) {
            DerateAmountModel object = new DerateAmountModel();
            for (Map.Entry<String, String> cellUnit : map.entrySet()) {
                String cellValue = cellUnit.getValue();
                ImportExcelConfigItem importExcelConfigItem = headMap.get(cellUnit.getKey());
                dataimpBaseService.parseCellMap(object, cellValue, importExcelConfigItem, errorList, rowIndex, 1);
            }
            idList.add(object.getId());
            mapDerateAmoubt.put(object.getId(), object);
            rowIndex++;
        }
        List<BasicCaseApply> basicCaseApplies = getFindById(idList);
        if (basicCaseApplies.size() > 0) {
            for (BasicCaseApply basicCaseApply : basicCaseApplies) {
                if (mapDerateAmoubt.containsKey(basicCaseApply.getId())) {
                    DerateAmountModel derateAmountModel = mapDerateAmoubt.get(basicCaseApply.getId());
                    basicCaseApply.setDerateRealAmount(derateAmountModel.getDerateRealAmount());
                    basicCaseApply.setApprovedMemo(derateAmountModel.getApprovedMemo());
                    String content = "";
                    if (derateAmountModel.getApprovedStatus().equals("是")) {
                        basicCaseApply.setApprovalStatus(ApprovalStatus.APPROVED_COMPLETED);
                        basicCaseApply.setApprovedResult(ApprovalResult.APPROVED_PASS);
                        basicCaseApply.setApprovedMemo(derateAmountModel.getApprovedMemo());
                        content = "欠款人[" + basicCaseApply.getPersonalName() + "]的减免审批通过, 通过减免的金额" + derateAmountModel.getDerateRealAmount();
                    } else {
                        basicCaseApply.setApprovalStatus(ApprovalStatus.APPROVED_COMPLETED);
                        basicCaseApply.setApprovedResult(ApprovalResult.APPROVED_REJECT);
                        basicCaseApply.setApprovedMemo(derateAmountModel.getApprovedMemo());
                        content = "欠款人[" + basicCaseApply.getPersonalName() + "]的减免审批拒绝";
                    }
                    dataimpBaseService.sendMessage(content, operatorModel, "协催减免审批",
                            MessageType.APPLY_APPROVE_MSG, MessageMode.POPUP);
                }
            }
        }
        if (basicCaseApplies.size() > 0) {
            savebasicCaseApplyListList(basicCaseApplies);
        }
    }

    /***
     * 改变导入查账案件状态
     * @param objClassList
     * @param inputStream
     */
    private void setcheckAccountApply(List<Class<?>> objClassList, InputStream inputStream, OperatorModel operatorModel) {
        List<Map<String, String>> sheetDataList = parseExcelAssistManagementData(inputStream);
        Map<String, ImportExcelConfigItem> headMap = excelConfigItemToMap(sheetDataList, inputStream, objClassList);
        sheetDataList.remove(sheetDataList.get(0));
        List<String> errorList = new ArrayList<>();
        List<String> idList = new ArrayList<>();
        Map<String, CheckAccountModel> mapDerateAmoubt = new HashMap<>();
        Long rowIndex = 1L;
        for (Map<String, String> map : sheetDataList) {
            CheckAccountModel object = new CheckAccountModel();
            for (Map.Entry<String, String> cellUnit : map.entrySet()) {
                String cellValue = cellUnit.getValue();
                ImportExcelConfigItem importExcelConfigItem = headMap.get(cellUnit.getKey());
                dataimpBaseService.parseCellMap(object, cellValue, importExcelConfigItem, errorList, rowIndex, 1);
            }
            idList.add(object.getId());
            mapDerateAmoubt.put(object.getId(), object);
            rowIndex++;
        }
        List<BasicCaseApply> basicCaseApplies = getFindById(idList);
        if (basicCaseApplies.size() > 0) {
            for (BasicCaseApply basicCaseApply : basicCaseApplies) {
                if (mapDerateAmoubt.containsKey(basicCaseApply.getId())) {
                    CheckAccountModel checkAccountModel = mapDerateAmoubt.get(basicCaseApply.getId());
                    basicCaseApply.setHasPayAmount(checkAccountModel.getHasPaymentAmount());
                    basicCaseApply.setLatestOverdueAmount(checkAccountModel.getLatestLeftAmount());
                    basicCaseApply.setBackCardNo(checkAccountModel.getBackCardNo());
                    basicCaseApply.setApprovedMemo(checkAccountModel.getFallbackRemark());
                    basicCaseApply.setApprovalStatus(ApprovalStatus.APPROVED_COMPLETED);
                    basicCaseApply.setApprovedResult(ApprovalResult.APPROVED_PASS);
                    String content = "欠款人[" + basicCaseApply.getPersonalName() + "]的查账回复完成, 近期还款金额为" + basicCaseApply.getDerateRealAmount();
                    dataimpBaseService.sendMessage(content, operatorModel, "协催查账回复",
                            MessageType.APPLY_APPROVE_MSG, MessageMode.POPUP);
                }
            }
        }
        if (basicCaseApplies.size() > 0) {
            savebasicCaseApplyListList(basicCaseApplies);
        }
    }

    /***
     * 改变导入报案申请案件状态
     * @param objClassList
     * @param inputStream
     */
    private void setReportCaseApply(List<Class<?>> objClassList, InputStream inputStream, OperatorModel operatorModel) {
        List<Map<String, String>> sheetDataList = parseExcelAssistManagementData(inputStream);
        Map<String, ImportExcelConfigItem> headMap = excelConfigItemToMap(sheetDataList, inputStream, objClassList);
        sheetDataList.remove(sheetDataList.get(0));
        List<String> errorList = new ArrayList<>();
        List<String> idList = new ArrayList<>();
        Map<String, ReportCaseModel> mapReportCase = new HashMap<>();
        Long rowIndex = 1L;
        for (Map<String, String> map : sheetDataList) {
            ReportCaseModel object = new ReportCaseModel();
            for (Map.Entry<String, String> cellUnit : map.entrySet()) {
                String cellValue = cellUnit.getValue();
                ImportExcelConfigItem importExcelConfigItem = headMap.get(cellUnit.getKey());
                dataimpBaseService.parseCellMap(object, cellValue, importExcelConfigItem, errorList, rowIndex, 1);
            }
            idList.add(object.getId());
            mapReportCase.put(object.getId(), object);
            rowIndex++;
        }
        List<BasicCaseApply> basicCaseApplies = getFindById(idList);
        if (basicCaseApplies.size() > 0) {
            for (BasicCaseApply basicCaseApply : basicCaseApplies) {
                if (mapReportCase.containsKey(basicCaseApply.getId())) {
                    ReportCaseModel reportCaseModel = mapReportCase.get(basicCaseApply.getId());
                    basicCaseApply.setApprovedMemo(reportCaseModel.getReportRemark());
                    String content = "";
                    if (reportCaseModel.getApprovedStatus().equals("是")) {
                        basicCaseApply.setApprovalStatus(ApprovalStatus.APPROVED_COMPLETED);
                        basicCaseApply.setApprovedResult(ApprovalResult.APPROVED_PASS);
                        basicCaseApply.setApprovedMemo(reportCaseModel.getReportRemark());
                        content = "欠款人[" + basicCaseApply.getPersonalName() + "]的报案申请已通过";
                    } else {
                        basicCaseApply.setApprovalStatus(ApprovalStatus.APPROVED_COMPLETED);
                        basicCaseApply.setApprovedResult(ApprovalResult.APPROVED_REJECT);
                        basicCaseApply.setApprovedMemo(reportCaseModel.getReportRemark());
                        content = "欠款人[" + basicCaseApply.getPersonalName() + "]的报案申请已拒绝";
                    }
                    dataimpBaseService.sendMessage(content, operatorModel, "协催报案审批",
                            MessageType.APPLY_APPROVE_MSG, MessageMode.POPUP);
                }
            }
        }
        if (basicCaseApplies.size() > 0) {
            savebasicCaseApplyListList(basicCaseApplies);
        }
    }

    /***
     * 改变导入留案申请案件状态
     * @param objClassList
     * @param inputStream
     */
    private void setLeaveCaseApply(List<Class<?>> objClassList, InputStream inputStream, OperatorModel operatorModel) {
        List<Map<String, String>> sheetDataList = parseExcelAssistManagementData(inputStream);
        Map<String, ImportExcelConfigItem> headMap = excelConfigItemToMap(sheetDataList, inputStream, objClassList);
        sheetDataList.remove(sheetDataList.get(0));
        List<String> errorList = new ArrayList<>();
        List<String> idList = new ArrayList<>();
        Map<String, LeaveCaseModel> mapLeaveCase = new HashMap<>();
        Long rowIndex = 1L;
        for (Map<String, String> map : sheetDataList) {
            LeaveCaseModel object = new LeaveCaseModel();
            for (Map.Entry<String, String> cellUnit : map.entrySet()) {
                String cellValue = cellUnit.getValue();
                ImportExcelConfigItem importExcelConfigItem = headMap.get(cellUnit.getKey());
                dataimpBaseService.parseCellMap(object, cellValue, importExcelConfigItem, errorList, rowIndex, 1);
            }
            idList.add(object.getId());
            mapLeaveCase.put(object.getId(), object);
            rowIndex++;
        }
        List<BasicCaseApply> basicCaseApplies = getFindById(idList);
        List<String> listIds = new ArrayList<>();
        basicCaseApplies.forEach(basicCaseApply -> listIds.add(basicCaseApply.getCaseId()));
        Map<String, BaseCase> findBaseCaseAllById = getFindBaseCaseAllById(listIds);
        List<BaseCase> listBaseCase = new ArrayList<>();
        if (basicCaseApplies.size() > 0) {
            for (BasicCaseApply basicCaseApply : basicCaseApplies) {
                if (mapLeaveCase.containsKey(basicCaseApply.getId())) {
                    LeaveCaseModel reportCaseModel = mapLeaveCase.get(basicCaseApply.getId());
                    String content = "";
                    if (reportCaseModel.getApprovedStatus().equals("是")) {
                        basicCaseApply.setApprovalStatus(ApprovalStatus.APPROVED_COMPLETED);
                        basicCaseApply.setApprovedResult(ApprovalResult.APPROVED_PASS);
                        basicCaseApply.setApprovedMemo(reportCaseModel.getApprovedMemo());
                        saveBaseCase(findBaseCaseAllById, basicCaseApply, reportCaseModel, listBaseCase, CaseLeaveFlag.HAS_LEAVE);
                        content = "欠款人[" + basicCaseApply.getPersonalName() + "]的留案申请已通过";
                    } else {
                        basicCaseApply.setApprovalStatus(ApprovalStatus.APPROVED_COMPLETED);
                        basicCaseApply.setApprovedResult(ApprovalResult.APPROVED_REJECT);
                        basicCaseApply.setApprovedMemo(reportCaseModel.getApprovedMemo());
                        saveBaseCase(findBaseCaseAllById, basicCaseApply, reportCaseModel, listBaseCase, CaseLeaveFlag.NO_LEAVE);
                        content = "欠款人[" + basicCaseApply.getPersonalName() + "]的留案申请已拒绝";
                    }
                    dataimpBaseService.sendMessage(content, operatorModel, "协催留案审批",
                            MessageType.APPLY_APPROVE_MSG, MessageMode.POPUP);
                }
            }
        }
        if (basicCaseApplies.size() > 0) {
            savebasicCaseApplyListList(basicCaseApplies);
        }
        if (listBaseCase.size() > 0) {
            baseCaseRepository.saveAll(listBaseCase);
        }
    }
    /***
     * 导入保存，每1000条保存一次
     * @param basicCaseApplyList
     */
    private void savebasicCaseApplyListList(List<BasicCaseApply> basicCaseApplyList){

        if(basicCaseApplyList.size() < 1000){
            basicCaseApplyRepository.saveAll(basicCaseApplyList);
            basicCaseApplyList.clear();
        }else {
            List<BasicCaseApply> list = new ArrayList<>();
            list.addAll(basicCaseApplyList.subList(0,1000));
            basicCaseApplyRepository.saveAll(list);
            basicCaseApplyList.removeAll(list);
        }
        if(basicCaseApplyList.size() > 0){
            savebasicCaseApplyListList(basicCaseApplyList);
        }
    }
    /**
     * 保存留案
     *
     * @param findBaseCaseAllById
     * @param basicCaseApply
     * @param reportCaseModel
     * @param listBaseCase
     */
    private void saveBaseCase(Map<String, BaseCase> findBaseCaseAllById, BasicCaseApply basicCaseApply, LeaveCaseModel reportCaseModel, List<BaseCase> listBaseCase, CaseLeaveFlag caseLeaveFlag) {
        if (findBaseCaseAllById.containsKey(basicCaseApply.getCaseId())) {
            BaseCase baseCase = findBaseCaseAllById.get(basicCaseApply.getCaseId());
            baseCase.setLeaveFlag(caseLeaveFlag);
            baseCase.setEndCaseDate(reportCaseModel.getEndCaseDate());
            listBaseCase.add(baseCase);
        }
    }

    /***
     * 根据id查询申请案件
     * @param idList
     * @return
     */
    private List<BasicCaseApply> getFindById(List<String> idList) {
        Iterable<BasicCaseApply> allById = basicCaseApplyRepository.findAllById(idList);
        List<BasicCaseApply> list = new ArrayList<>();
        if (allById.iterator().hasNext()) {
            list = IteratorUtils.toList(allById.iterator());
        } else {
            throw new BadRequestException(null, "basicCaseApply", "basicCaseApply.is.not.exist");
        }
        return list;
    }

    /***
     * 根据id查询申请案件
     * @param idList
     * @return
     */
    private Map<String, BaseCase> getFindBaseCaseAllById(List<String> idList) {
        Iterable<BaseCase> allById = baseCaseRepository.findAllById(idList);
        Map<String, BaseCase> map = new HashMap<>();
        List<BaseCase> list = new ArrayList<>();
        if (allById.iterator().hasNext()) {
            list = IteratorUtils.toList(allById.iterator());
            list.forEach(baseCase -> map.put(baseCase.getId(), baseCase));
        } else {
            throw new BadRequestException(null, "basicCaseApply", "basicCaseApply.is.not.exist");
        }
        return map;
    }

    /***
     * Excel解析数据
     * @param inputStream
     * @return
     */
    private List<Map<String, String>> parseExcelAssistManagementData(InputStream inputStream) {

        Map<Integer, List<Map<String, String>>> dataMap = null;
        List<Map<String, String>> sheetDataList = null;
        try {
            logger.info("解析数据文件开始........");
            StopWatch watch = new StopWatch();
            watch.start();
            //数据信息
            dataMap = SaxParseExcelUtil.parseExcel(inputStream, 1, 1, -1, 1);
            watch.stop();
            logger.info("解析数据文件结束，耗时：{}", watch.getTotalTimeMillis());
            //获取第一个Sheet的数据
            sheetDataList = dataMap.get(1);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return sheetDataList;
    }

    /**
     * 将申请的Excel Model转化为MAP
     *
     * @param inputStream
     * @return
     */
    private Map<String, ImportExcelConfigItem> excelConfigItemToMap(List<Map<String, String>> heardList, InputStream inputStream, List<Class<?>> objClassList) {

        List<CaseInfoPropertyResponse> responses = caseImportExcelTempService.getObjejctPro(objClassList);
        Map<String, ImportExcelConfigItem> itemMap = new HashMap<>();
        if (heardList.size() > 0) {
            Map<String, String> map = heardList.get(0);
            for (CaseInfoPropertyResponse response : responses) {
                ImportExcelConfigItem item = new ImportExcelConfigItem();
                for (Map.Entry<String, String> filed : map.entrySet()) {
                    if (response.getName().equals(filed.getValue())) {
                        BeanUtils.copyProperties(response, item);
                        item.setCol(filed.getKey());
                    }
                }
                itemMap.put(item.getCol(), item);
            }
        }
        return itemMap;
    }

}

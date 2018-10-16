package cn.fintecher.pangolin.service.dataimp.service;

import cn.fintecher.pangolin.common.enums.*;
import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.model.TaskBoxModel;
import cn.fintecher.pangolin.common.model.UploadFile;
import cn.fintecher.pangolin.common.model.WebSocketMessageModel;
import cn.fintecher.pangolin.common.model.response.LoginResponse;
import cn.fintecher.pangolin.common.utils.InnerServiceUrl;
import cn.fintecher.pangolin.common.utils.Snowflake;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.common.annotation.ExcelAnno;
import cn.fintecher.pangolin.entity.domain.*;
import cn.fintecher.pangolin.entity.managentment.ImportExcelConfigItem;
import cn.fintecher.pangolin.service.dataimp.client.WebSocketClient;
import cn.fintecher.pangolin.service.dataimp.repository.*;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

/**
 * @Author:peishouwen
 * @Desc: 导入服务基础Service
 * @Date:Create in 13:23 2018/7/29
 */
@Service("dataimpBaseService")
public class DataimpBaseService {

    Logger logger= LoggerFactory.getLogger(DataimpBaseService.class);

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    WebSocketClient webSocketClient;

    @Autowired
    CaseTransferLogRepository caseTransferLogRepository;

    @Autowired
    private AssistCaseRepository assistCaseRepository;

    @Autowired
    private AssistCaseApplyRepository assistCaseApplyRepository;

    @Autowired
    private BasicCaseApplyRepository caseApplyRepository;

    @Autowired
    private PublicCaseRepository publicCaseRepository;


    /**
     * 获取用户信息
     * @param token
     * @return
     * @throws Exception
     */
    public OperatorModel getUserByToken(String token){
        //获取用户信息
        ResponseEntity<LoginResponse> exchange = restTemplate.exchange(InnerServiceUrl.MANAGEMENT_SERVICE_GETBYTOKEN.concat(token), HttpMethod.GET, null, LoginResponse.class);
        OperatorModel operator = exchange.getBody().getUser();
        if (Objects.isNull(operator)) {
            throw new BadRequestException(null, "operator", "operator.not.login");
        }
        return operator;
    }

    /**
     * 将模板配置转化为MAP
     * @param items
     * @param sheetIndex
     * @return
     */
    public Map<String ,ImportExcelConfigItem> excelConfigItemToMap(List<ImportExcelConfigItem> items, Integer sheetIndex){
        Map<String ,ImportExcelConfigItem> itemMap=new HashMap<>();
        for(ImportExcelConfigItem item :items){
            if(sheetIndex.equals(item.getSheetNum())){
                itemMap.put(item.getCol(),item);
            }
        }
        return itemMap;
    }

    /**
     * 解析每行数据转为对应的实体对象
     */
    public void parseCellMap(Object obj, String cellValue,ImportExcelConfigItem importExcelConfigItem,List<String> errorList,long rowIndex,int sheetIndex)  {
        try {
                Field field =obj.getClass().getDeclaredField(importExcelConfigItem.getAttribute());
                //打开实体中私有变量的权限
                field.setAccessible(true);
                Object object = fomatValue(field,cellValue,errorList,rowIndex,importExcelConfigItem.getCol(),sheetIndex);
                field.set(obj, object);

        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
    }

    /**
     * 格式化数据
     * @param field
     * @param cellValue
     * @return
     */
    private Object fomatValue(Field field,String cellValue,List<String> errorList,long rowIndex,String colStr,int sheetNum){
        if(StringUtils.isEmpty(cellValue)){
            return null;
        }
        Object obj=null;
        ExcelAnno.FieldDataType fieldDataType = field.getAnnotation(ExcelAnno.class).fieldDataType();
        switch (fieldDataType) {
            case STRING:
                obj=cellValue;
                break;
            case INTEGER:
                try {
                    obj=Integer.parseInt(cellValue);
                } catch (NumberFormatException e) {
                    logger.error(e.getMessage(),e);
                    errorList.add(createErrorStr( cellValue, "数值", rowIndex, colStr,sheetNum));
                }
                break;
            case DOUBLE:
                try {
                    BigDecimal bigDecimal=new BigDecimal(cellValue.replaceAll(",",""));
                    obj = Math.abs(bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
                } catch (NumberFormatException e) {
                    logger.error(e.getMessage(),e);
                    errorList.add(createErrorStr( cellValue, "数值", rowIndex, colStr,sheetNum));
                }
                break;
            case DATE:
                try {
                    if (cellValue.matches("\\d{4}/\\d{1,2}/\\d{1,2}")) {
                        obj= ZWDateUtil.getUtilDate(cellValue, "yyyy/MM/dd");
                    } else if (cellValue.matches("\\d{4}-\\d{2}-\\d{2}")) {
                        obj=ZWDateUtil.getUtilDate(cellValue, "yyyy-MM-dd");
                    } else if (cellValue.matches("^\\d{4}\\d{2}\\d{2}")) {
                        obj=ZWDateUtil.getUtilDate(cellValue, "yyyyMMdd");
                    } else if (cellValue.matches("\\d{4}.\\d{1,2}.\\d{1,2}")) {
                        obj=ZWDateUtil.getUtilDate(cellValue, "yyyy.MM.dd");
                    } else if(cellValue.matches("\\d{1,2}/\\d{1,2}/\\d{4}")){
                        obj= ZWDateUtil.getUtilDate(cellValue, "MM/dd/yyyy");
                    }else if(cellValue.matches("\\d{1,2}/\\d{1,2}/\\d{2}")){
                        obj= ZWDateUtil.getUtilDate(cellValue, "M/dd/yy");
                    }else {
                        obj=null;
                    }
                    break;
                }catch (Exception e){
                    logger.error(e.getMessage(),e);
                    errorList.add(createErrorStr( cellValue, "日期", rowIndex, colStr,sheetNum));
                }
            default:
                obj=cellValue;
                break;
        }
        return obj;
    }

    /**
     * 生成错误日志
     * @param resultDataList
     * @return
     * @throws IOException
     */
    public String createImpErrorFile(List<String> resultDataList) throws IOException {
        //生成错误信息txt
        String filePath = FileUtils.getTempDirectoryPath().concat(File.separator).concat(
                ZWDateUtil.fomratterDate(ZWDateUtil.getNowDateTime(),"yyyyMMddHHmmss")).concat(".txt");
        File file=new File(filePath);
        if(file.exists()){
            file.delete();
        }else {
            file.createNewFile();
        }
        BufferedWriter out =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"));
        for(String str:resultDataList){
            out.write(str);
            out.newLine();
        }
        if (out != null) {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //开始文件上传
        //文件上传
        FileSystemResource resource = new FileSystemResource(filePath);
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("file", resource);
        ResponseEntity<UploadFile> response= restTemplate.postForEntity(InnerServiceUrl.COMMON_SERVICE_UPLOADFILE, param, UploadFile.class);
       return response.getBody().getId();
    }


    /**
     * 拼装错误信息
     * @param cellValue
     * @param remark
     * @param rowIndex
     * @param colStr
     * @return
     */
    private String createErrorStr(String cellValue,String remark,long rowIndex,String colStr,int sheetNum){
        return  "第[".concat(String.valueOf(sheetNum)).concat("]sheet页的第[").concat(String.valueOf(rowIndex)).concat("]行,第[").concat(colStr).concat("]列的值[").
                concat(cellValue).concat("]转为[").concat(remark).concat("]");
    }

    public String createErrorBlank(String titleName,String colStr,int sheetNum){
        return  "第[".concat(String.valueOf(sheetNum)).concat("]sheet页的,第[").concat(colStr).concat("]列的[").
                concat(titleName).concat("]的不能空");
    }

    public BoolQueryBuilder getQbByBatchNumber(String batchNumber){
        BoolQueryBuilder queryBuilder= QueryBuilders.boolQuery();
        queryBuilder.must(matchPhraseQuery("batchNumber",batchNumber));
        return queryBuilder;
    }

    public BoolQueryBuilder getQbByBatchNumberAndOperator(String batchNumber,String operator){
        BoolQueryBuilder queryBuilder= QueryBuilders.boolQuery();
        queryBuilder.must(matchPhraseQuery("batchNumber",batchNumber));
        queryBuilder.must(matchPhraseQuery("operator",operator));
        return queryBuilder;
    }


    /**
     *
     * @param content 消息类型
     * @param operator 操作人
     * @param title 消息标题
     * @param messageType 消息类型
     * @param messageMode 消息model
     */
    public void sendMessage(String content, OperatorModel operator, String title, MessageType messageType,
                            MessageMode messageMode){
        //发送消息
        try{
            WebSocketMessageModel webSocketMessageModel=new WebSocketMessageModel();
            webSocketMessageModel.setMessageType(messageType);
            webSocketMessageModel.setTitle(title);
            webSocketMessageModel.setContent(content);
            webSocketMessageModel.setMsgDate(ZWDateUtil.getNowDateTime());
            webSocketMessageModel.setMessageMode(messageMode);
            webSocketClient.sendMsgByUserId(webSocketMessageModel,operator.getUsername());
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
    }


    /**
     *
     * @param content 消息类型
     * @param userName 操作人
     * @param title 消息标题
     * @param messageType 消息类型
     * @param messageMode 消息model
     */
    public void sendMessage(String content, String userName, String title, MessageType messageType,
                            MessageMode messageMode){
        //发送消息
        try{
            WebSocketMessageModel webSocketMessageModel=new WebSocketMessageModel();
            webSocketMessageModel.setMessageType(messageType);
            webSocketMessageModel.setTitle(title);
            webSocketMessageModel.setContent(content);
            webSocketMessageModel.setMsgDate(ZWDateUtil.getNowDateTime());
            webSocketMessageModel.setMessageMode(messageMode);
            webSocketClient.sendMsgByUserId(webSocketMessageModel,userName);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
    }

    /***
     * 任务盒子
     * @param taskBoxType 盒子类型
     * @param taskBoxStatus 消息状态
     * @param operatorModel 登录用户model
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    public TaskBoxModel sendTaskBoxMessage(TaskBoxModel taskBoxModel, TaskBoxType taskBoxType, TaskBoxStatus taskBoxStatus,
                                   String fileId, String fileName,OperatorModel operatorModel, Date startTime, Date endTime, String taskDescribe){

        taskBoxModel.setTaskBoxType(taskBoxType);
        taskBoxModel.setTaskBoxStatus(taskBoxStatus);
        taskBoxModel.setFileId(fileId);
        taskBoxModel.setFileName(fileName);
        taskBoxModel.setOperator(operatorModel.getId());
        taskBoxModel.setOperatorTime(ZWDateUtil.getNowDateTime());
        if(Objects.nonNull(taskDescribe)){
            taskBoxModel.setTaskDescribe(taskDescribe);
        }
        if(Objects.nonNull(startTime)){
            taskBoxModel.setBeginDate(startTime);
        }
        if(Objects.nonNull(endTime)){
            taskBoxModel.setEndDate(endTime);
        }
        webSocketClient.sendTaskByUserId(taskBoxModel, operatorModel.getUsername());
        return taskBoxModel;
    }

    /***
     *上传路径
     * @param filePath
     * @return
     */
    public UploadFile getFileUrl(String filePath){
        FileSystemResource resource = new FileSystemResource(filePath);
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("file", resource);
        ResponseEntity<UploadFile> response= restTemplate.postForEntity(InnerServiceUrl.COMMON_SERVICE_UPLOADFILE, param, UploadFile.class);
        return response.getBody();
    }
    /**
     *添加案件流转日志
     * @param caseId
     * @param operContent
     * @param loginResponse
     * @return
     */
    public CaseTransferLog createLog(String caseId,String operContent,LoginResponse loginResponse){
        CaseTransferLog caseTransferLog=new CaseTransferLog();
        caseTransferLog.setCaseId(caseId);
        caseTransferLog.setOperContent(operContent);
        caseTransferLog.setUserName(loginResponse.getUser().getUsername());
        caseTransferLog.setFullName(loginResponse.getUser().getFullName());
        caseTransferLog.setOperatorTime(ZWDateUtil.getNowDateTime());
        return caseTransferLog;
    }

    /***
     * 结清/退案/停催时结束各个申请以及案件
     * @param caseIds
     */
    public void endApplyCase(List<String> caseIds){
        //检查是否存在各类申请
        endBasicApply(caseIds);
        //检查是否存在公共案件, 然后删除公共案件
        endPublicCase(caseIds);
        //检查是否存在协催案件申请
        retractAssistCaseApply(caseIds);
        //检查是否存在协催案件
        retractAssistCollectionCase(caseIds);
    }

    /***
     * 结束协催申请
     */
    public void retractAssistCaseApply(List<String> caseIds){
        logger.info("结束协催申请开始...");
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        qb.must(termsQuery("caseId", caseIds));
        qb.must(QueryBuilders.boolQuery().should(matchPhraseQuery("approveStatus", AssistApprovedStatus.ASSIST_WAIT_APPROVAL.toString()))
                .should(matchPhraseQuery("approveStatus", AssistApprovedStatus.LOCAL_WAIT_APPROVAL.toString())));
        //撤回协催申请案件
        Iterable<AssistCaseApply> search = assistCaseApplyRepository.search(qb);
        logger.debug(qb.toString());
        if(search.iterator().hasNext()){
            List<AssistCaseApply> applies = IteratorUtils.toList(search.iterator());
            applies.forEach(assistCaseApply -> {
                if(assistCaseApply.getAssistFlag().equals(AssistFlag.OFFSITE_OUT_ASSIST) ||
                        assistCaseApply.getAssistFlag().equals(AssistFlag.OFFSITE_PHONE_ASSIST)){
                    assistCaseApply.setApproveStatus(AssistApprovedStatus.ASSIST_COMPLETED);
                    assistCaseApply.setApproveResult(AssistApprovedResult.ASSIST_REJECT);
                }else{
                    assistCaseApply.setApproveStatus(AssistApprovedStatus.LOCAL_COMPLETED);
                    assistCaseApply.setApproveResult(AssistApprovedResult.LOCAL_REJECT);
                }
                assistCaseApply.setApproveMemo("案件导入自动停催");
            });
            assistCaseApplyRepository.saveAll(applies);
        }
    }

    /**
     * 结束协催案件
     * @param caseIds
     */
    public void retractAssistCollectionCase(List<String> caseIds){
        logger.info("结束协催案件开始...");
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        qb.must(termsQuery("caseId", caseIds));
        qb.mustNot(matchPhraseQuery("assistStatus", AssistStatus.ASSIST_COMPLETED.toString()));
        //撤回协催案件
        Iterable<AssistCollectionCase> assistStatus = assistCaseRepository.search(qb);
        if(assistStatus.iterator().hasNext()){
            List<AssistCollectionCase> applies = IteratorUtils.toList(assistStatus.iterator());
            applies.forEach(assistCase -> {
                assistCase.setAssistStatus(AssistStatus.ASSIST_COMPLETED);
            });
            assistCaseRepository.saveAll(applies);
        }
    }

    /***
     * 结束各类申请
     * @param caseIds
     */
    public void endBasicApply(List<String> caseIds){
        logger.info("结束各类申请开始...");
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        builder.must(termsQuery("caseId", caseIds)).must(matchPhraseQuery("approvalStatus", ApprovalStatus.WAIT_APPROVAL.toString()));
        Iterable<BasicCaseApply> search = caseApplyRepository.search(builder);
        if(search.iterator().hasNext()){
            List<BasicCaseApply> basicCaseApplies = IteratorUtils.toList(search.iterator());
            basicCaseApplies.forEach(basicCaseApply -> {
                basicCaseApply.setApprovalStatus(ApprovalStatus.APPROVED_COMPLETED);
                basicCaseApply.setApprovedMemo("案件导入自动停催");
                basicCaseApply.setApprovedResult(ApprovalResult.APPROVED_REJECT);
            });
            caseApplyRepository.saveAll(basicCaseApplies);
        }
    }

    /***
     * 结束公共案件
     * @param caseIds
     */
    public void endPublicCase(List<String> caseIds){
        logger.info("结束公共案件开始...");
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        builder.must(termsQuery("caseId", caseIds));
        Iterable<PublicCase> search = publicCaseRepository.search(builder);
        if(search.iterator().hasNext()){
            List<PublicCase> publicCases = IteratorUtils.toList(search.iterator());
            publicCaseRepository.deleteAll(publicCases);
        }
    }

}

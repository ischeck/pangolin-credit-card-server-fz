package cn.fintecher.pangolin.service.management.service;


import cn.fintecher.pangolin.common.annotation.ExcelAnno;
import cn.fintecher.pangolin.common.enums.Marital;
import cn.fintecher.pangolin.common.enums.OperatorState;
import cn.fintecher.pangolin.common.enums.Sex;
import cn.fintecher.pangolin.common.enums.UserState;
import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.CaseInfoPropertyResponse;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.model.UserImportModel;
import cn.fintecher.pangolin.common.utils.BeanUtils;
import cn.fintecher.pangolin.common.utils.InnerServiceUrl;
import cn.fintecher.pangolin.common.utils.SaxParseExcelUtil;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.managentment.*;
import cn.fintecher.pangolin.service.management.repository.OperatorRepository;
import cn.fintecher.pangolin.service.management.repository.OrganizationRepository;
import cn.fintecher.pangolin.service.management.repository.UserRegisterRepository;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by huyanmin on 2018/8/29.
 */
@Service("userRegisterService")
public class UserRegisterService {
    final Logger log = LoggerFactory.getLogger(UserRegisterService.class);

    @Autowired
    private UserRegisterRepository userRegisterRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OperatorRepository operatorRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    /**
     * 检查是否存在用户
     *
     * @param employeeNumber
     * @return
     */
    public void checkEmployeeNumber(String employeeNumber) {
        QUser qUser = QUser.user;
        Boolean exists = userRegisterRepository.exists(qUser.employeeNumber.eq(employeeNumber).and(qUser.state.eq(UserState.INCUMBENCY)));
        if(exists){
            throw new BadRequestException(null, "employeeNumber","employeeNumber.is.exist");
        }
    }

    /***
     * 验证该用户是否存在协催或催收案件
     * @param operatorId
     */
    public void checkOperatorHasCollectionCase(String operatorId){
        ParameterizedTypeReference<List<String>> responseType = new ParameterizedTypeReference<List<String>>() {
        };
        ResponseEntity<List<String>> entity = restTemplate.exchange(InnerServiceUrl.DOMIAN_SERVICE_SEARCHBASECASE.concat(operatorId),
                HttpMethod.GET, null, responseType);
        List<String> body = entity.getBody();
        if(body.size()>0){
            throw new BadRequestException(null, "baseCase","operator.has.baseCase");
        }
    }

    /***
     * 验证该用户是否存在协催或催收案件
     * @param user
     */
    public Operator checkHasCollectionCase(User user){
        Iterable<Operator> all = operatorRepository.findAll(QOperator.operator.employeeNumber.eq(user.getEmployeeNumber())
                .and(QOperator.operator.state.eq(OperatorState.ENABLED)));
        if(all.iterator().hasNext()){
            Operator operator = all.iterator().next();
            //验证是否存在协催或在催收的案件
            checkOperatorHasCollectionCase(operator.getId());
            return operator;
        }
        return null;
    }

    /**
     * 用户导入
     * @param inputStream
     * @param operatorModel
     */
    public void userImport(InputStream inputStream, OperatorModel operatorModel) {
        log.info("用户导入开始...");
        List<Map<String, String>> sheetDataList = parseExcelAssistManagementData(inputStream);
        List<Class<?>> objClassList = new ArrayList<>();
        objClassList.add(UserImportModel.class);
        Map<String, ImportExcelConfigItem> headMap = excelConfigItemToMap(sheetDataList, objClassList);
        sheetDataList.remove(sheetDataList.get(0));
        List<String> errorList = new ArrayList<>();
        List<String> idList = new ArrayList<>();
        List<UserImportModel> userImportModels = new ArrayList<>();
        Long rowIndex = 1L;
        for (Map<String, String> map : sheetDataList) {
            UserImportModel userImportModel = new UserImportModel();
            for (Map.Entry<String, String> cellUnit : map.entrySet()) {
                String cellValue = cellUnit.getValue();
                ImportExcelConfigItem importExcelConfigItem = headMap.get(cellUnit.getKey());
                parseCellMap(userImportModel, cellValue, importExcelConfigItem, errorList, rowIndex, 1);
            }
            idList.add(userImportModel.getOrganizationId());
            userImportModels.add(userImportModel);
            rowIndex++;
        }
        log.info("解析到的用户model的个数:"+userImportModels.size());
        if(userImportModels.size() > 0){
            List<User> userList = new ArrayList<>();
            for(UserImportModel userImportModel: userImportModels){
                User user = new User();
                BeanUtils.copyProperties(userImportModel, user);
                user.setSex(transferSex(userImportModel.getSex()));
                user.setMarital(transferMarital(userImportModel.getMarital()));
                user.setState(transferUserState(userImportModel.getState()));
                user.setOperator(operatorModel.getEmployeeNumber());
                user.setOperatorTime(new Date());
                user.setOrganization(userImportModel.getOrganizationId());
                userList.add(user);
            }
            if(userList.size()>0){
                saveUserList(userList);
            }
        }
    }

    /***
     * 用户导入保存，每1000条保存一次
     * @param userList
     */
    private void saveUserList(List<User> userList){

        if(userList.size() < 1000){
            userRegisterRepository.saveAll(userList);
            userList.clear();
        }else {
            List<User> list = new ArrayList<>();
            list.addAll(userList.subList(0,1000));
            userRegisterRepository.saveAll(list);
            userList.removeAll(list);
        }
        if(userList.size() > 0){
            saveUserList(userList);
        }
    }

    /**
     * 转换用户状态
     * @param userState
     * @return
     */
    private UserState transferUserState(String userState){
        UserState userStateTemp = null;
        if(Objects.nonNull(userState)){
            if(userState.equals("离职")) {
                userStateTemp = UserState.DIMISSION;
            }else {
                userStateTemp = UserState.INCUMBENCY;
            }
        }
        return userStateTemp;
    }

    /***
     * 转换婚姻状况
     * @param martial
     * @return
     */
    private Marital transferMarital(String martial){
        Marital maritalTemp = null;
        if(Objects.nonNull(martial)){
            if (martial.equals("未婚")){
                maritalTemp = Marital.UNMARRIED;
            }else if(martial.equals("已婚")){
                maritalTemp = Marital.MARRIED;
            }else {
                maritalTemp = Marital.UNKNOWN;
            }
        }else {
            maritalTemp = Marital.UNKNOWN;
        }
        return maritalTemp;
    }

    /**
     * 转换性别
     * @param sex
     * @return
     */
    private Sex transferSex(String sex){
        Sex sexTemp = null;
        if(Objects.nonNull(sex)){
           if (sex.equals("男")){
               sexTemp = Sex.MALE;
           }else if(sex.equals("女")){
               sexTemp = Sex.FEMALE;
           }else {
               sexTemp = Sex.UNKNOWN;
           }
        }
        return sexTemp;
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
            log.info("解析数据文件开始........");
            StopWatch watch = new StopWatch();
            watch.start();
            //数据信息
            dataMap = SaxParseExcelUtil.parseExcel(inputStream, 1, 1, -1, 1);
            watch.stop();
            log.info("解析数据文件结束，耗时：{}", watch.getTotalTimeMillis());
            //获取第一个Sheet的数据
            sheetDataList = dataMap.get(1);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return sheetDataList;
    }

    /**
     * 将Excel中的表头转化为MAP
     *
     * @param heardList
     * @return
     */
    private Map<String, ImportExcelConfigItem> excelConfigItemToMap(List<Map<String, String>> heardList, List<Class<?>> objClassList) {

        List<CaseInfoPropertyResponse> responses = getObjejctPro(objClassList);
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

    /**
     * 获取指定类的属性
     *
     * @param objClassList
     * @return
     */
    public List<CaseInfoPropertyResponse> getObjejctPro(List<Class<?>> objClassList) {
        List<CaseInfoPropertyResponse> caseInfoPropertyResponseList = new ArrayList<>();
        for (Class<?> objClass : objClassList) {
            //获取类中所有的字段
            Field[] fields = objClass.getDeclaredFields();
            for (Field field : fields) {
                //获取标记了ExcelAnno的注解字段
                if (field.isAnnotationPresent(ExcelAnno.class)) {
                    ExcelAnno f = field.getAnnotation(ExcelAnno.class);
                    CaseInfoPropertyResponse caseInfoPropertyResponse = new CaseInfoPropertyResponse();
                    caseInfoPropertyResponse.setAttribute(field.getName());
                    caseInfoPropertyResponse.setName(f.cellName());
                    caseInfoPropertyResponse.setPropertyType(f.fieldType().name());
                    caseInfoPropertyResponseList.add(caseInfoPropertyResponse);
                }
            }
        }

        return caseInfoPropertyResponseList;
    }

    /**
     * 解析每行数据转为对应的实体对象
     */
    public void parseCellMap(Object obj, String cellValue, ImportExcelConfigItem importExcelConfigItem, List<String> errorList, long rowIndex, int sheetIndex) {
        try {
            Field field = obj.getClass().getDeclaredField(importExcelConfigItem.getAttribute());
            //打开实体中私有变量的权限
            field.setAccessible(true);
            Object object = fomatValue(field, cellValue, errorList, rowIndex, importExcelConfigItem.getCol(), sheetIndex);
            field.set(obj, object);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 格式化数据
     *
     * @param field
     * @param cellValue
     * @return
     */
    private Object fomatValue(Field field, String cellValue, List<String> errorList, long rowIndex, String colStr, int sheetNum) {
        if (StringUtils.isEmpty(cellValue)) {
            return null;
        }
        Object obj = null;
        ExcelAnno.FieldDataType fieldDataType = field.getAnnotation(ExcelAnno.class).fieldDataType();
        switch (fieldDataType) {
            case STRING:
                obj = cellValue;
                break;
            case INTEGER:
                try {
                    obj = Integer.parseInt(cellValue);
                } catch (NumberFormatException e) {
                    log.error(e.getMessage(), e);
                }
                break;
            case DOUBLE:
                try {
                    BigDecimal bigDecimal = new BigDecimal(cellValue.replaceAll(",", ""));
                    obj = Math.abs(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                } catch (NumberFormatException e) {
                    log.error(e.getMessage(), e);
                }
                break;
            case DATE:
                try {
                    if (cellValue.matches("\\d{4}/\\d{1,2}/\\d{1,2}")) {
                        obj = ZWDateUtil.getUtilDate(cellValue, "yyyy/MM/dd");
                    } else if (cellValue.matches("\\d{4}-\\d{2}-\\d{2}")) {
                        obj = ZWDateUtil.getUtilDate(cellValue, "yyyy-MM-dd");
                    } else if (cellValue.matches("^\\d{4}\\d{2}\\d{2}")) {
                        obj = ZWDateUtil.getUtilDate(cellValue, "yyyyMMdd");
                    } else if (cellValue.matches("\\d{4}.\\d{1,2}.\\d{1,2}")) {
                        obj = ZWDateUtil.getUtilDate(cellValue, "yyyy.MM.dd");
                    } else if (cellValue.matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
                        obj = ZWDateUtil.getUtilDate(cellValue, "MM/dd/yyyy");
                    } else if (cellValue.matches("\\d{1,2}/\\d{1,2}/\\d{2}")) {
                        obj = ZWDateUtil.getUtilDate(cellValue, "M/dd/yy");
                    } else {
                        obj = null;
                    }
                    break;
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            default:
                obj = cellValue;
                break;
        }
        return obj;
    }

    /**
     * 读取文件
     * @param fileId
     * @return
     */
    public InputStream readFile(String fileId){
        //获取文件
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<byte[]> response = restTemplate.exchange(InnerServiceUrl.COMMON_SERVICE_GETFILEBYID.concat("/").concat(fileId),
                HttpMethod.GET, new HttpEntity<byte[]>(headers), byte[].class);
        List<String> content=response.getHeaders().get("Content-Disposition");
        if(Objects.isNull(content) || content.isEmpty()){
            throw new BadRequestException(null, "importExcelData","templateFile.is.illegal");
        }
        if(!content.get(0).endsWith(SaxParseExcelUtil.EXCEL_TYPE_XLSX)){
            log.error("fileName: {} ",content.get(0));
            throw   new BadRequestException(null, "importExcelData","file.format.error");
        }
        byte[] result = response.getBody();
        InputStream inputStream=new ByteArrayInputStream(result);
        return inputStream;
    }

}
package cn.fintecher.pangolin.service.repair.service;

import cn.fintecher.pangolin.common.annotation.ExcelAnno;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.model.response.LoginResponse;
import cn.fintecher.pangolin.common.utils.InnerServiceUrl;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.managentment.ImportExcelConfigItem;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

/**
 * @Author:hanwannan
 * @Desc: 数据修复服务基础Service
 * @Date:Create in 13:23 2018/9/1
 */
@Service("dataRepairBaseService")
public class DataRepairBaseService {

    Logger logger= LoggerFactory.getLogger(DataRepairBaseService.class);

    @Autowired
    RestTemplate restTemplate;


    /**
     * 获取用户信息
     * @param token
     * @return
     * @throws Exception
     */
    public OperatorModel getUserByToken(String token) throws Exception{
        //获取用户信息
        ResponseEntity<LoginResponse> exchange = restTemplate.exchange(InnerServiceUrl.MANAGEMENT_SERVICE_GETBYTOKEN.concat(token), HttpMethod.GET, null, LoginResponse.class);
        OperatorModel operator = exchange.getBody().getUser();
        if (Objects.isNull(operator)) {
            throw new Exception("operator.not.login");
        }
        return operator;
    }


    /**
     * 格式化数据
     * @param field
     * @param cellValue
     * @return
     */
    private Object fomatValue(Field field,String cellValue,List<String> errorList,long rowIndex,String colStr,int sheetNum){
        if(org.apache.commons.lang3.StringUtils.isEmpty(cellValue)){
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
                    BigDecimal bigDecimal=new BigDecimal(cellValue);
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
                        logger.error("日期格式无法匹配: {}",cellValue);
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

}

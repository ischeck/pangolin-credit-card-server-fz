package cn.fintecher.pangolin.service.dataimp.service;

import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.utils.MD5;
import cn.fintecher.pangolin.common.utils.Snowflake;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.domain.CaseChangeCityImportTemp;
import cn.fintecher.pangolin.entity.domain.ImportOthersDataExcelRecord;
import cn.fintecher.pangolin.common.annotation.ExcelAnno;
import cn.fintecher.pangolin.entity.managentment.ImportExcelConfigItem;
import cn.fintecher.pangolin.service.dataimp.repository.CaseChangeCityImportTempRepository;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 16:45 2018/8/2
 */
@Service("parseCellToCaseChangeCityTask")
public class ParseCellToCaseChangeCityTask {
    Logger logger= LoggerFactory.getLogger(ParseCellToCaseChangeCityTask.class);

    @Autowired
    DataimpBaseService dataimpBaseService;

    @Autowired
    CaseChangeCityImportTempRepository caseChangeCityImportTempRepository;
    @Async
    public CompletableFuture<List<String>> cellDataToObject(List<Map<String,String>> sheetDataList , ImportOthersDataExcelRecord record, Map<String ,ImportExcelConfigItem> itemMap,
                                                            int pageNo, int pageSize, OperatorModel operatorModel, int sheetIndex) {
        logger.info("处理第 {} sheet页的第{}页数据开始........", sheetIndex, pageNo);
        Snowflake snowflake=new Snowflake((int) Thread.currentThread().getId()%1024);
        List<String> errorList = new ArrayList<>();
        List<CaseChangeCityImportTemp> caseChangeCityImportTempList=new ArrayList<>();
        for (int i = 0; i < sheetDataList.size(); i++) {
            Map<String, String> rowMap = sheetDataList.get(i);
            CaseChangeCityImportTemp caseChangeCityImportTemp = new CaseChangeCityImportTemp();
            String primaryKey = "";
            Map<String,String> primarykeyMap=new HashMap<>();
            Map<String, String> remarkMap = new HashMap<>();
            for (Map.Entry<String, String> cellMap : rowMap.entrySet()) {
                String col = cellMap.getKey();
                String cellValue = cellMap.getValue();
                ImportExcelConfigItem importExcelConfigItem = itemMap.get(col);
                if (Objects.isNull(importExcelConfigItem) || StringUtils.isBlank(importExcelConfigItem.getAttribute())) {
                    //无对应的数据字段
                    remarkMap.put(importExcelConfigItem.getTitleName(), cellValue);
                } else {
                    if (importExcelConfigItem.getPropertyType().equals(ExcelAnno.FieldType.CASE.name())) {
                        if (importExcelConfigItem.isKeyFlag()) {
                            primarykeyMap.put(importExcelConfigItem.getAttribute(),cellValue);
                        }
                        dataimpBaseService.parseCellMap(caseChangeCityImportTemp, cellValue, importExcelConfigItem, errorList, pageNo * pageSize + 1,sheetIndex);
                    }
                }
            }
            caseChangeCityImportTemp.setId(String.valueOf(snowflake.next()));
            Map<String, String> finalMap = new LinkedHashMap<>();
            primarykeyMap.entrySet().stream().sorted(Map.Entry.<String, String>comparingByKey()
                    .reversed()).forEachOrdered(e -> finalMap.put(e.getKey(), e.getValue()));
            for(Map.Entry<String,String> entry:finalMap.entrySet()){
                primaryKey=primaryKey.concat(entry.getValue());
            }
            primarykeyMap.clear();
            finalMap.clear();
            caseChangeCityImportTemp.setPrimaryKey(MD5.MD5Encode(primaryKey.concat(caseChangeCityImportTemp.getBatchNumber())));
            caseChangeCityImportTemp.setRemarkMap(remarkMap);
            caseChangeCityImportTemp.setPrincipalId(record.getPrincipalId());
            caseChangeCityImportTemp.setPrincipalName(record.getPrincipalName());
            caseChangeCityImportTemp.setOperator(operatorModel.getUsername());
            caseChangeCityImportTemp.setOperatorTime(ZWDateUtil.getNowDateTime());
            caseChangeCityImportTemp.setOperBatchNumber(record.getOperBatchNumber());
            caseChangeCityImportTempList.add(caseChangeCityImportTemp);
        }
        if(errorList.isEmpty()){
            caseChangeCityImportTempRepository.saveAll(caseChangeCityImportTempList);
        }
        logger.info("处理第 {} sheet页的第{}页数据完成........",sheetIndex,pageNo);
        return CompletableFuture.completedFuture(errorList);
    }
}

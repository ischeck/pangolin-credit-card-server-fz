package cn.fintecher.pangolin.service.dataimp.service;

import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.utils.MD5;
import cn.fintecher.pangolin.common.utils.Snowflake;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.domain.CaseWarnImportTemp;
import cn.fintecher.pangolin.entity.domain.ImportOthersDataExcelRecord;
import cn.fintecher.pangolin.common.annotation.ExcelAnno;
import cn.fintecher.pangolin.entity.managentment.ImportExcelConfigItem;
import cn.fintecher.pangolin.service.dataimp.repository.CaseWarnImportTempRepository;
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
@Service("parseCellToCaseWarnTask")
public class ParseCellToCaseWarnTask {
    Logger logger= LoggerFactory.getLogger(ParseCellToCaseWarnTask.class);

    @Autowired
    DataimpBaseService dataimpBaseService;

    @Autowired
    CaseWarnImportTempRepository caseWarnImportTempRepository;
    @Async
    public CompletableFuture<List<String>> cellDataToObject(List<Map<String,String>> sheetDataList , ImportOthersDataExcelRecord record, Map<String ,ImportExcelConfigItem> itemMap,
                                                            int pageNo, int pageSize, OperatorModel operatorModel, int sheetIndex) {
        logger.info("处理第 {} sheet页的第{}页数据开始........", sheetIndex, pageNo);
        List<String> errorList = new ArrayList<>();
        Snowflake snowflake=new Snowflake((int)Thread.currentThread().getId()%1024);
        List<CaseWarnImportTemp> caseWarnImportTempArrayList=new ArrayList<>();
        for (int i = 0; i < sheetDataList.size(); i++) {
            Map<String, String> rowMap = sheetDataList.get(i);
            CaseWarnImportTemp caseWarnImportTemp = new CaseWarnImportTemp();
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
                        dataimpBaseService.parseCellMap(caseWarnImportTemp, cellValue, importExcelConfigItem, errorList, pageNo * pageSize + 1,sheetIndex);
                    }
                }
            }
            caseWarnImportTemp.setId(String.valueOf(snowflake.next()));
            Map<String, String> finalMap = new LinkedHashMap<>();
            primarykeyMap.entrySet().stream().sorted(Map.Entry.<String, String>comparingByKey()
                    .reversed()).forEachOrdered(e -> finalMap.put(e.getKey(), e.getValue()));
            for(Map.Entry<String,String> entry:finalMap.entrySet()){
                primaryKey=primaryKey.concat(entry.getValue());
            }
            primarykeyMap.clear();
            finalMap.clear();
            caseWarnImportTemp.setPrimaryKey(MD5.MD5Encode(primaryKey.concat(caseWarnImportTemp.getBatchNumber())));
            caseWarnImportTemp.setRemarkMap(remarkMap);
            caseWarnImportTemp.setPrincipalId(record.getPrincipalId());
            caseWarnImportTemp.setPrincipalName(record.getPrincipalName());
            caseWarnImportTemp.setOperator(operatorModel.getUsername());
            caseWarnImportTemp.setOperatorTime(ZWDateUtil.getNowDateTime());
            caseWarnImportTemp.setOperBatchNumber(record.getOperBatchNumber());
            caseWarnImportTempArrayList.add(caseWarnImportTemp);
        }
        if(errorList.isEmpty()){
            caseWarnImportTempRepository.saveAll(caseWarnImportTempArrayList);
        }
        logger.info("处理第 {} sheet页的第{}页数据完成........",sheetIndex,pageNo);
        return CompletableFuture.completedFuture(errorList);
    }
}

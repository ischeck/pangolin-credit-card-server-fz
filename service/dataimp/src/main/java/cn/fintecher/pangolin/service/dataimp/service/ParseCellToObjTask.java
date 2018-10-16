package cn.fintecher.pangolin.service.dataimp.service;

import cn.fintecher.pangolin.common.enums.ManagementType;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.model.RemarkModel;
import cn.fintecher.pangolin.common.utils.MD5;
import cn.fintecher.pangolin.common.utils.Snowflake;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.domain.BaseCaseAllImportExcelTemp;
import cn.fintecher.pangolin.entity.domain.BasePersonalImportExcelTemp;
import cn.fintecher.pangolin.common.annotation.ExcelAnno;
import cn.fintecher.pangolin.entity.managentment.ImportExcelConfigItem;
import cn.fintecher.pangolin.service.dataimp.model.request.BaseCaseImportExcelRequest;
import cn.fintecher.pangolin.service.dataimp.repository.BaseCaseImportExcelTempRepository;
import cn.fintecher.pangolin.service.dataimp.repository.BasePersonalImportExcelTempRepository;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @Author:peishouwen
 * @Desc: 将Excel对象转化为javaBean
 * @Date:Create in 17:25 2018/7/27
 */
@Service
public class ParseCellToObjTask {
    Logger logger= LoggerFactory.getLogger(ParseCellToObjTask.class);
    @Autowired
    BaseCaseImportExcelTempRepository baseCaseImportExcelTempRepository;

    @Autowired
    BasePersonalImportExcelTempRepository basePersonalImportExcelTempRepository;

    @Autowired
    DataimpBaseService dataimpBaseService;


    @Async
    public CompletableFuture<List<String>> cellDataToObject(List<Map<String,String>> sheetDataList , Class<?> baseCaseClass, Class<?> personalClass, Map<String ,ImportExcelConfigItem> itemMap,
                                                            BaseCaseImportExcelRequest baseCaseImportExcelRequest, String batchNumber,
                                                            int sheetTotals, int pageNo, int pageSize,OperatorModel operatorModel,int sheetIndex) {
        logger.info("处理第 {} sheet页的第{}页数据开始........",sheetIndex,pageNo);
        Snowflake snowflake=new Snowflake((int)Thread.currentThread().getId()%1024);
        List<BaseCaseAllImportExcelTemp> baseCaseDataList=new ArrayList<>();
        List<BasePersonalImportExcelTemp> basePersonalDataList=new ArrayList<>();
        List<String> errorList=new ArrayList<>();
        try {
            for(int i=0;i<sheetDataList.size();i++){
                Map<String,String> rowMap=sheetDataList.get(i);
                BaseCaseAllImportExcelTemp caseObj=(BaseCaseAllImportExcelTemp)baseCaseClass.newInstance();
                boolean caseObjFlag=false;
                BasePersonalImportExcelTemp personalObj=(BasePersonalImportExcelTemp)personalClass.newInstance();
                boolean personalObjFlag=false;
                Set<RemarkModel> remarkMap=new HashSet<>();
                String primarykey="";
                String relationId="";
                int rowIndex=(pageNo-1)*pageSize+i+1;
                Map<String,String> primarykeyMap=new HashMap<>();
                Map<String ,ImportExcelConfigItem> itemMapClone=new HashMap<>();
                itemMapClone.putAll(itemMap);
                for(Map.Entry<String,String> cellMap : rowMap.entrySet()){
                    String col=cellMap.getKey();
                    String cellValue=cellMap.getValue();
                    ImportExcelConfigItem importExcelConfigItem=itemMapClone.get(col);
                    itemMapClone.remove(col);
                    if(Objects.isNull(importExcelConfigItem) || StringUtils.isBlank(importExcelConfigItem.getAttribute())){
                        //无对应的数据字段
                        RemarkModel remarkModel=new RemarkModel();
                        remarkModel.setKey(Objects.isNull(importExcelConfigItem.getTitleName()) ? col : importExcelConfigItem.getTitleName());
                        remarkModel.setValue(cellValue);
                        remarkModel.setHideFlag(importExcelConfigItem.getHideFlag());
                        remarkModel.setSort(importExcelConfigItem.getSort());
                        remarkMap.add(remarkModel);
                    }else{
                        //案件基本信息
                        if(importExcelConfigItem.getPropertyType().equals(ExcelAnno.FieldType.CASE.name())){
                            if(importExcelConfigItem.isKeyFlag()){
                                primarykeyMap.put(importExcelConfigItem.getAttribute(),cellValue);
                                if(sheetTotals==1){
                                    relationId=relationId.concat(cellValue);
                                }
                            }
                            if(sheetTotals!=1) {
                                if (importExcelConfigItem.isRelationFlag()) {
                                    relationId = relationId.concat(cellValue);
                                }
                            }
                            caseObjFlag=true;
                            caseObj.setSequenceNo(Long.parseLong(String.valueOf(rowIndex)));
                            dataimpBaseService.parseCellMap(caseObj,cellValue,importExcelConfigItem,errorList,rowIndex,sheetIndex);
                        }else {
                            //客户基本信息
                            if(sheetTotals>1){
                                if(importExcelConfigItem.isRelationFlag()){
                                    relationId=relationId.concat(cellValue);
                                }
                            }
                            personalObjFlag=true;
                            dataimpBaseService.parseCellMap(personalObj,cellValue, importExcelConfigItem,errorList,rowIndex,sheetIndex);
                        }
                    }
                }
                //空置校验
                if(!itemMapClone.isEmpty()){
                    itemMapClone.forEach((key,value)->{
                        if(value.getBlankFlag().equals(ManagementType.NO)){
                            errorList.add(dataimpBaseService.createErrorBlank(value.getTitleName(),value.getCol(),sheetIndex));
                        }
                    });
                }
                if(caseObjFlag){
                    caseObj.setId(String.valueOf(snowflake.next()));
                    Map<String, String> finalMap = new LinkedHashMap<>();
                    primarykeyMap.entrySet().stream().sorted(Map.Entry.<String, String>comparingByKey()
                            .reversed()).forEachOrdered(e -> finalMap.put(e.getKey(), e.getValue()));
                    for(Map.Entry<String,String> entry:finalMap.entrySet()){
                        primarykey=primarykey.concat(entry.getValue());
                    }
                    primarykeyMap.clear();
                    finalMap.clear();
                    caseObj.setPrimaryKey(MD5.MD5Encode(primarykey.concat(batchNumber)));
                    caseObj.setSheetTotals(sheetTotals);
                    caseObj.setRemarkMap(remarkMap);
                    //手工输入或系统自动生成
                    caseObj.setBatchNumber(batchNumber);
                    caseObj.setPrincipalId(baseCaseImportExcelRequest.getPrincipalId());
                    if(StringUtils.isBlank(caseObj.getPrincipalName())){
                        caseObj.setPrincipalName(baseCaseImportExcelRequest.getPrincipalName());
                    }
                    caseObj.setOperator(operatorModel.getUsername());
                    caseObj.setOperatorTime(ZWDateUtil.getNowDateTime());
                    caseObj.setRelationPersonalId(MD5.MD5Encode(relationId.concat(batchNumber)));
                    //欠款金额
                    if(Objects.isNull(caseObj.getOverdueAmtTotal()) || caseObj.getOverdueAmtTotal() == 0.0){
                        caseObj.setOverdueAmtTotal(caseObj.getLeftAmt());
                    }
                    if(Objects.isNull(caseObj.getOverdueAmtTotalDollar())  || caseObj.getOverdueAmtTotalDollar() == 0.0){
                        caseObj.setOverdueAmtTotalDollar(caseObj.getLeftAmtDollar());
                    }
                    if(Objects.isNull(caseObj.getLeftAmt()) || caseObj.getLeftAmt() == 0.0){
                        caseObj.setLeftAmt(caseObj.getOverdueAmtTotal());
                    }
                    if(Objects.isNull(caseObj.getLeftAmtDollar()) || caseObj.getLeftAmtDollar() == 0.0){
                        caseObj.setLeftAmtDollar(caseObj.getOverdueAmtTotalDollar());
                    }
                    //委案日期
                    if(Objects.isNull(caseObj.getDelegationDate())){
                        caseObj.setDelegationDate(Objects.isNull(baseCaseImportExcelRequest.getDelegationDate()) ? ZWDateUtil.getNowDate() : baseCaseImportExcelRequest.getDelegationDate());
                    }
                    //结案日期(为空时自动补3个月)
                    if(Objects.isNull(caseObj.getEndCaseDate())){
                        caseObj.setEndCaseDate(ZWDateUtil.getAppointDate(caseObj.getDelegationDate(),3, ChronoUnit.MONTHS));
                    }
                    baseCaseDataList.add(caseObj);
                }
                if(personalObjFlag){
                    personalObj.setId(String.valueOf(snowflake.next()));
                    personalObj.setRelationId(MD5.MD5Encode(relationId.concat(batchNumber)));
                    personalObj.setBatchNumber(batchNumber);
                    basePersonalDataList.add(personalObj);
                }

            }
            if(errorList.isEmpty()){
                if(!baseCaseDataList.isEmpty()){
                    baseCaseImportExcelTempRepository.saveAll(baseCaseDataList);
                    baseCaseDataList.clear();
                }
                if(!basePersonalDataList.isEmpty()){
                    basePersonalImportExcelTempRepository.saveAll(basePersonalDataList);
                    basePersonalDataList.clear();
                }
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        logger.info("处理第 {} sheet页的第{}页数据完成........",sheetIndex,pageNo);
        return CompletableFuture.completedFuture(errorList);
    }





}

package cn.fintecher.pangolin.service.repair.service;

import cn.fintecher.pangolin.common.enums.ApplyFileContent;
import cn.fintecher.pangolin.entity.repair.*;
import cn.fintecher.pangolin.service.repair.model.SpecialTransferDataImportModel;
import cn.fintecher.pangolin.service.repair.model.request.DataRepairImportRequest;
import cn.fintecher.pangolin.service.repair.respository.*;
import cn.fintecher.pangolin.service.repair.utils.ImportExcelUntil;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author:hanwannan
 * @Desc:
 * @Date:Create in 9:48 2018/9/1
 */
@Service("dataRepairImportService")
public class DataRepairImportService {
    Logger logger=LoggerFactory.getLogger(DataRepairImportService.class);

    //户籍资料导入字段匹配模板
    private static String kosikiDataTemplateMap= 
    		"{\"姓名\":\"name\","
    		+ "\"身份证号\":\"idNo\","
    		+ "\"银行\":\"bank\","
    		+ "\"申调地区\":\"applyTransferArea\","
    		+ "\"户籍\":\"koseki\","
    		+ "\"户籍地区\":\"kosekiArea\","
    		+ "\"申调时间\":\"applyTransferTime\","
    		+ "\"备注\":\"remark\","
    		+ "\"服务处所\":\"serviceSpace\","
    		+ "\"联系方式\":\"contact\","
    		+ "\"新地址\":\"newAddress\","
    		+ "\"曾用名\":\"usedName\"}";
    //户籍备注导入字段匹配模板
    private static String kosikiRemarkTemplateMap=
            "{\"姓名\":\"name\","
            + "\"身份证号\":\"idNo\","
            + "\"银行\":\"bank\","
            + "\"申调地区\":\"applyTransferArea\","
            + "\"申调时间\":\"applyTransferTime\","
            + "\"户籍地址\":\"kosekiAddress\","
            + "\"备注\":\"remark\"}";
    //通讯资料导入字段匹配模板
    private static String communicationDataTemplateMap=
            "{\"姓名\":\"name\","
            + "\"身份证号\":\"idNo\","
            + "\"银行\":\"bank\","
            + "\"申调地区\":\"applyTransferArea\","
            + "\"座机\":\"landLinePhone\","
            + "\"手机\":\"mobile\","
            + "\"地址\":\"address\","
            + "\"申调时间\":\"applyTransferTime\","
            + "\"备注/欠款金额\":\"remark\","
            + "\"电话类型\":\"type\"}";
    //社保资料导入字段匹配模板
    private static String socialSecurityDataTemplateMap=
            "{\"姓名\":\"name\","
            + "\"身份证号\":\"idNo\","
            + "\"银行\":\"bank\","
            + "\"申调地区\":\"applyTransferArea\","
            + "\"申调时间\":\"applyTransferTime\","
            + "\"户籍地址\":\"kosekiAddress\","
            + "\"社保号\":\"socialSecurityNo\","
            + "\"参保时间\":\"attendSecurityTime\","
            + "\"参保状态\":\"attendSecurityStatus\","
            + "\"最近缴纳时间\":\"latelyPayTime\","
            + "\"最近缴费基数\":\"latelyPayBase\","
            + "\"工作单位\":\"workUnit\","
            + "\"公司地址\":\"companyAddress\","
            + "\"公司电话\":\"companyPhone\","
            + "\"备注\":\"remark\"}";
    //关联关系导入字段匹配模板
    private static String relationshipTemplateMap=
            "{\"姓名\":\"name\","
            + "\"身份证号\":\"idNo\","
            + "\"关系\":\"relation\","
            + "\"关系人姓名\":\"relationPersonName\","
            + "\"关系人身份证号\":\"relationPersonIdNo\"}";
    //村委资料导入字段匹配模板
    private static String villageCommitteeDataTemplateMap=
            "{\"省份\":\"province\","
            + "\"城市\":\"city\","
            + "\"区/县\":\"area\","
            + "\"镇/乡\":\"town\","
            + "\"村/居委会\":\"village\","
            + "\"地区码\":\"areaCode\","
            + "\"查询人\":\"queryMan\","
            + "\"查询日期\":\"queryDate\","
            + "\"联系人\":\"linkman\","
            + "\"职务\":\"position\","
            + "\"办公电话\":\"officePhone\","
            + "\"手机\":\"mobile\","
            + "\"家庭电话\":\"homePhone\","
            + "\"备注\":\"remark\"}";
    //特调资料导入字段匹配模板
    private static String specialTransferDataTemplateMap=
            "{\"姓名\":\"name\","
            + "\"身份证号\":\"idNo\","
            + "\"银行\":\"bank\","
            + "\"申调地区\":\"applyTransferArea\","
            + "\"文件名\":\"fileName\","
            + "\"与本人关系\":\"relationship\","
            + "\"证件类型\":\"type\","
            + "\"户籍地区\":\"kosekiArea\","
            + "\"查询日期\":\"queryDate\","
            + "\"备注/金额\":\"remark\""
            + "\"特调地区\":\"specialTransferArea\"}";

    //计生资料导入字段匹配模板
    private static String familyPlanningAreaMap =
            "{\"姓名\":\"name\","
                    + "\"身份证号\":\"idNo\","
                    + "\"银行\":\"bank\","
                    + "\"申调地区\":\"applyTransferArea\","
                    + "\"申调时间\":\"applyTransferTime\","
                    + "\"计生地址\":\"familyPlanningArea\","
                    + "\"备注\":\"remark\"}";

    @Autowired
    private KosekiDataRepository kosekiDataRepository;
    @Autowired
    private KosekiRemarkRepository kosekiRemarkRepository;
    @Autowired
    private RelationshipRepository relationshipRepository;
    @Autowired
    private SocialSecurityDataRepository socialSecurityDataRepository;
    @Autowired
    private CommunicationDataRepository communicationDataRepository;
    @Autowired
    private VillageCommitteeDataRepository villageCommitteeDataRepository;
    @Autowired
    private SpecialTransferDataRepository specialTransferDataRepository;
    @Autowired
    private FamilyPlanningDataRepository familyPlanningDataRepository;

    /**
     * 数据修复资料导入
     * @param request
     * @param inputStream
     * @throws Exception
     */
    @Async
    public void importExcelData(DataRepairImportRequest request, InputStream inputStream, List<String> errorList) throws Exception {
        logger.info("资料导入开始.......");
        StopWatch watch = new StopWatch();
        watch.start();

        if(request.getImportContentType()== ApplyFileContent.HOUSEHOLD_REGISTER_RESOURCE){
            //导入户籍资料
            StopWatch watch2 = new StopWatch();
            watch2.start();
            logger.info("解析数据文件开始........");
            List<KosekiData> data=ImportExcelUntil.importExcel(KosekiData.class,kosikiDataTemplateMap,inputStream,errorList);
            watch2.stop();
            logger.info("解析数据文件结束{}",watch2.getTotalTimeMillis());
            
            if(data!=null&&data.size()>0) {
            		kosekiDataRepository.saveAll(data);
	        }
        }else if (request.getImportContentType()== ApplyFileContent.HOUSEHOLD_REGISTER_REMARK){
            //导入户籍备注
            StopWatch watch2 = new StopWatch();
            watch2.start();
            logger.info("解析数据文件开始........");
            List<KosekiRemark> data=ImportExcelUntil.importExcel(KosekiRemark.class,kosikiRemarkTemplateMap,inputStream,errorList);
            watch2.stop();
            logger.info("解析数据文件结束{}",watch2.getTotalTimeMillis());
            if(data!=null&&data.size()>0) {
            		kosekiRemarkRepository.saveAll(data);
            }
        }else if (request.getImportContentType()== ApplyFileContent.CONTACT_RELATIONSHIP){
            //导入关联关系
            StopWatch watch2 = new StopWatch();
            watch2.start();
            logger.info("解析数据文件开始........");
            List<Relationship> data=ImportExcelUntil.importExcel(Relationship.class,relationshipTemplateMap,inputStream,errorList);
            watch2.stop();
            logger.info("解析数据文件结束{}",watch2.getTotalTimeMillis());
            if(data!=null&&data.size()>0) {
            		relationshipRepository.saveAll(data);
	        }
        }else if (request.getImportContentType()== ApplyFileContent.SOCIAL_SECURITY){
            //导入社保资料
            StopWatch watch2 = new StopWatch();
            watch2.start();
            logger.info("解析数据文件开始........");
            List<SocialSecurityData> data=ImportExcelUntil.importExcel(SocialSecurityData.class,socialSecurityDataTemplateMap,inputStream,errorList);
            watch2.stop();
            logger.info("解析数据文件结束{}",watch2.getTotalTimeMillis());
            if(data!=null&&data.size()>0) {
            		socialSecurityDataRepository.saveAll(data);
	        }
        }else if (request.getImportContentType()== ApplyFileContent.PHONE_RESOURCE){
            //导入通讯资料
            StopWatch watch2 = new StopWatch();
            watch2.start();
            logger.info("解析数据文件开始........");
            List<CommunicationData> data=ImportExcelUntil.importExcel(CommunicationData.class,communicationDataTemplateMap,inputStream,errorList);
            watch2.stop();
            logger.info("解析数据文件结束{}",watch2.getTotalTimeMillis());
            
            if(data!=null&&data.size()>0) {
            		communicationDataRepository.saveAll(data);
	        }
        }else if (request.getImportContentType()== ApplyFileContent.TOWN_RESOURCE){
            //导入村委资料
            StopWatch watch2 = new StopWatch();
            watch2.start();
            logger.info("解析数据文件开始........");
            List<VillageCommitteeData> data=ImportExcelUntil.importExcel(VillageCommitteeData.class,villageCommitteeDataTemplateMap,inputStream,errorList);
            watch2.stop();
            logger.info("解析数据文件结束{}",watch2.getTotalTimeMillis());
            data.forEach(villageCommitteeData -> {
                villageCommitteeData.setAddress((Objects.nonNull(villageCommitteeData.getProvince())?villageCommitteeData.getProvince():"")
                        .concat(Objects.nonNull(villageCommitteeData.getCity())?villageCommitteeData.getCity():"")
                        .concat(Objects.nonNull(villageCommitteeData.getArea())?villageCommitteeData.getArea():"")
                        .concat(Objects.nonNull(villageCommitteeData.getTown())?villageCommitteeData.getTown():"")
                        .concat(Objects.nonNull(villageCommitteeData.getVillage())?villageCommitteeData.getVillage():""));
            });
            if(data!=null&&data.size()>0) {
            		villageCommitteeDataRepository.saveAll(data);
	        }
            
        }else if (request.getImportContentType()== ApplyFileContent.SPECIAL_RESOURCE){
            //导入特调资料
            StopWatch watch2 = new StopWatch();
            watch2.start();
            logger.info("解析数据文件开始........");
            List<SpecialTransferDataImportModel> data=ImportExcelUntil.importExcel(SpecialTransferDataImportModel.class,specialTransferDataTemplateMap,inputStream,errorList);
            watch2.stop();
            logger.info("解析数据文件结束{}",watch2.getTotalTimeMillis());
            if(data!=null&&data.size()>0) {
            		saveImportingSpecialTransferData(data);
	        }
        } else {
            //导入计生资料
            StopWatch watch2 = new StopWatch();
            watch2.start();
            logger.info("解析数据文件开始........");
            List<FamilyPlanningData> data = ImportExcelUntil.importExcel(FamilyPlanningData.class, familyPlanningAreaMap, inputStream, errorList);
            watch2.stop();
            logger.info("解析数据文件结束{}", watch2.getTotalTimeMillis());
            if (data.size() > 0) {
                familyPlanningDataRepository.saveAll(data);
            }
        }
        watch.stop();
        logger.info("资料导入结束，耗时{}",watch.getTotalTimeMillis());
    }
    /**
     * 保存特调资料
     * @param data
     * @throws Exception
     */
    private void saveImportingSpecialTransferData(List<SpecialTransferDataImportModel> data) throws Exception{
        List<SpecialTransferData> list = new ArrayList<>();
        for(int i=0;i<data.size();i++){
            SpecialTransferDataImportModel model=data.get(i);
            SpecialTransferData tempStd = new SpecialTransferData();
            tempStd.setName(model.getName());
            tempStd.setIdNo(model.getIdNo());
            tempStd.setBank(model.getBank());
            tempStd.setApplyTransferArea(model.getApplyTransferArea());
            tempStd.setKosekiArea(model.getKosekiArea());
            tempStd.setQueryDate(model.getQueryDate());
            tempStd.setRemark(model.getRemark());
            Credential credential=new Credential();
            credential.setFileId(model.getFileName());
            credential.setRelationship(model.getRelationship());
            credential.setType(model.getType());
            List<Credential> credentialList=tempStd.getCredentialSet();
            if(credentialList==null){
                credentialList=new ArrayList<>();
            }
            credentialList.add(credential);
            tempStd.setCredentialSet(credentialList);
            list.add(tempStd);
        }
        if(list.size()>0){
            specialTransferDataRepository.saveAll(list);
        }
    }

    /**
     * 根据idNo 获取特调资料
     * @param idNo
     * @return
     * @throws Exception
     */
    private SpecialTransferData getSpecialTransferDataByIdNo(String idNo) throws Exception {
        MatchQueryBuilder mqb= QueryBuilders.matchQuery("idNo",idNo);
        Iterable<SpecialTransferData> specialTransferDataIterable = specialTransferDataRepository.search(mqb);
        if(specialTransferDataIterable.iterator().hasNext()){
            return specialTransferDataIterable.iterator().next();
        }
        return null;
    }

}

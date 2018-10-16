package cn.fintecher.pangolin.service.dataimp.service;

import cn.fintecher.pangolin.common.enums.*;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.utils.*;
import cn.fintecher.pangolin.entity.domain.*;
import cn.fintecher.pangolin.entity.managentment.Principal;
import cn.fintecher.pangolin.service.dataimp.model.request.BaseCaseImportExcelRequest;
import cn.fintecher.pangolin.service.dataimp.repository.*;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.swing.text.html.parser.Entity;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

/**
 * @Author:peishouwen
 * @Desc: 案件确认执行
 * @Date:Create in 23:37 2018/7/31
 */
@Service("confirmExcelImpCaseSubTask")
public class ConfirmExcelImpCaseSubTask {
    Logger logger= LoggerFactory.getLogger(ConfirmExcelImpCaseSubTask.class);

    @Autowired
    BasePersonalImportExcelTempRepository basePersonalImportExcelTempRepository;

    @Autowired
    PersonalImpRepository personalImpRepository;

    @Autowired
    PersonalContactImpRepository personalContactImpRepository;

    @Autowired
    ImportBaseCaseRepository importBaseCaseRepository;



    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;


    @Async
    public CompletableFuture<Integer> doSubTask( List<BaseCaseAllImportExcelTemp> perBaseCaseList,OperatorModel operator,  Principal principal,int pageNo ) {
        logger.info("开始确认第 {} 页数据,数量{}",pageNo,perBaseCaseList.size());
        Integer confirmTotals=0;
        Snowflake snowflake=new Snowflake((int)Thread.currentThread().getId()%1024);
        //记录需要存储的数据
        List<Personal> personalSaveList=new ArrayList<>();
        List<BaseCase> baseCaseSaveList=new ArrayList<>();
        List<PersonalContact> personalContactSaveList=new ArrayList<>();
        List<String> caseDeleteList=new ArrayList<>();
        List<String> personalDeleteList=new ArrayList<>();
        //关联关系集合查询导入的客户信息
        Set<String> relationIdSets=new HashSet<>();
        //数据库中已经存在客户信息(证件号查询)
        Set<String> personalSets=new HashSet<>();
        try {
            //一次性查询出所有需要比对数据集
            for(BaseCaseAllImportExcelTemp baseObj:perBaseCaseList){
                relationIdSets.add(baseObj.getRelationPersonalId());
                if(!StringUtils.isBlank(baseObj.getCertificateNo())){
                    personalSets.add(baseObj.getCertificateNo());
                }
                caseDeleteList.add(baseObj.getId());
            }
            //查询已有的客户信息
            Map<String,Personal> personalMap=new HashMap<>();
            BoolQueryBuilder queryBuilder1= QueryBuilders.boolQuery();
            queryBuilder1.must().add(termsQuery("certificateNo.keyword",personalSets));
            Iterator<Personal> personalIterator=personalImpRepository.search(queryBuilder1).iterator();
            while (personalIterator.hasNext()){
                Personal personalTmp=  personalIterator.next();
                personalMap.put(personalTmp.getCertificateNo(),personalTmp);
            }
            //查询导入的客户信息
            BoolQueryBuilder queryBuilder2= QueryBuilders.boolQuery();
            queryBuilder2.must().add(termsQuery("relationId.keyword",relationIdSets));
            Iterator<BasePersonalImportExcelTemp> basePersonalImportExcelTempIterator= basePersonalImportExcelTempRepository.search(queryBuilder2).iterator();
            List<BasePersonalImportExcelTemp> basePersonalCaseList= Lists.newArrayList(basePersonalImportExcelTempIterator);
            for(BasePersonalImportExcelTemp basePersonalImportExcelTemp:basePersonalCaseList){
                personalDeleteList.add(basePersonalImportExcelTemp.getId());
            }
            StopWatch stopWatch4=new StopWatch();
            stopWatch4.start();
            //通过relationId分组
            Map<String,List<BasePersonalImportExcelTemp>> personalTmpGroup=basePersonalCaseList.stream().collect(Collectors.groupingBy(
                    BasePersonalImportExcelTemp::getRelationId));
            for(BaseCaseAllImportExcelTemp baseCaseTemp:perBaseCaseList){
                //多sheet页数据用关联字段查询
                //案件信息
                BaseCase baseCase=new BaseCase();
                ModelMapper modelMapper=new ModelMapper();
                modelMapper.map(baseCaseTemp,baseCase);
                baseCase.setIssuedFlag(CaseIssuedFlag.AREA_UN_DIS);
                //客户信息（已存在不做任何变更）
                if(personalMap.containsKey(baseCaseTemp.getCertificateNo())){
                    baseCase.setPersonal(personalMap.get(baseCaseTemp.getCertificateNo()));
                }else {
                    Personal personalTmp=new Personal();
                    personalSaveList.add(personalTmp);
                    baseCase.setPersonal(personalTmp);
                    List<BasePersonalImportExcelTemp> personalPerList=new ArrayList<>();
                    if(personalTmpGroup.isEmpty()){
                        //只添加本人联系信息
                        BasePersonalImportExcelTemp perTmp=new BasePersonalImportExcelTemp();
                        perTmp.setPersonalName(baseCaseTemp.getPersonalName());
                        perTmp.setCertificateNo(baseCaseTemp.getCertificateNo());
                        perTmp.setCertificateType(baseCaseTemp.getCertificateType());
                        perTmp.setAccount(baseCaseTemp.getAccount());
                        personalPerList.add(perTmp);
                    }else {
                        personalPerList= personalTmpGroup.get(baseCaseTemp.getRelationPersonalId());
                    }
                    if(!"浦发银行".equals(principal.getPrincipalName())){
                        for(int i=0;i<personalPerList.size();i++){
                            //客户基本信息
                            BasePersonalImportExcelTemp perObj=personalPerList.get(i);
                            ModelMapper modelMapper1=new ModelMapper();
                            modelMapper1.map(perObj,personalTmp);
                            //证件号
                            String certificateNo=StringUtils.isBlank(perObj.getCertificateNo()) ? baseCaseTemp.getCertificateNo() : perObj.getCertificateNo();
                            //证件类型
                            String certificateType=StringUtils.isBlank(perObj.getCertificateType()) ? baseCaseTemp.getCertificateType() :perObj.getCertificateType();
                            //客户姓名
                            String personalName=StringUtils.isBlank(perObj.getPersonalName()) ? baseCaseTemp.getPersonalName() : perObj.getPersonalName();

                            personalTmp.setCertificateType(certificateType);
                            personalTmp.setCertificateNo(certificateNo);
                            personalTmp.setPersonalName(personalName);
                            perObj.setCertificateNo(certificateNo);
                            perObj.setCertificateType(certificateType);
                            perObj.setPersonalName(personalName);
                            //联系人信息
                            createPersonalContactInf(perObj,snowflake,operator,personalTmp.getId(),personalContactSaveList);
                        }
                    }else {
                        //浦发银行特殊处理(只有本人信息)
                        //证件号来判断唯一性,浦发的需要特殊处理,处理规则：
                        //联系信息也需要后合并：type为联系属性：本人/联系人/亲属;
                        //地址类型：RELATION:公司/家里/手机/其它/户籍
                        personalTmp.setId(String.valueOf(snowflake.next()));
                        personalSaveList.add(personalTmp);
                        Map<String,PersonalContact> tmpContactMap=new HashMap<>();
                        for(int j=0;j<personalPerList.size();j++){
                            //客户基本信息
                            BasePersonalImportExcelTemp perObj=personalPerList.get(j);
                            String phoneNo=perObj.getSelfPhoneNo();
                            String addr=perObj.getHomeAddr();
                            String name=perObj.getPersonalName();
                            String certificateNo=perObj.getCertificateNo();
                            PersonalContact personalContact=null;
                            if(tmpContactMap.containsKey(name)){
                                personalContact=tmpContactMap.get(name);
                            }else {
                                personalContact=new PersonalContact();
                                personalContact.setPersonalId(personalTmp.getId());
                                personalContact.setId(String.valueOf(snowflake.next()));
                                personalContact.setName(name);
                                personalContact.setRelation(perObj.getRelationType());
                                personalContact.setSort(tmpContactMap.size()+1);
                                personalContact.setOperator(operator.getUsername());
                                personalContact.setOperatorTime(ZWDateUtil.getNowDateTime());
                                tmpContactMap.put(name,personalContact);
                            }

                            tmpContactMap.put(name,personalContact);
                            if("本人".equals(perObj.getRelationType())){
                                personalTmp.setPersonalName(name);
                                personalTmp.setCertificateNo(certificateNo);
                                createContactPF(personalTmp, perObj, phoneNo, addr, personalContact,snowflake);
                            }else {
                                //联系人信息
                                createContactCell(personalTmp, perObj, phoneNo, addr, personalContact,snowflake);
                            }
                        }
                        //合并联系人
                        for(Map.Entry<String, PersonalContact> entry : tmpContactMap.entrySet()){
                            personalContactSaveList.add(entry.getValue());
                        }
                    }
                }
                //委托信息
                baseCase.setPrincipal(principal);
                //案件信息
                baseCaseSaveList.add(baseCase);
            }
            stopWatch4.stop();
            logger.error("耗时4,{}",stopWatch4.getTotalTimeMillis());
            //更新数据到本地库
            //客户基本信息
            if(!personalSaveList.isEmpty()){
                List<List<Personal>> parts = Lists.partition(personalSaveList, 1000);
                parts.stream().forEach(list -> personalImpRepository.saveAll(list));
            }
            //客户联系人信息
            if(!personalContactSaveList.isEmpty()){
                List<List<PersonalContact>> parts= Lists.partition(personalContactSaveList,1000);
                parts.stream().forEach(list -> personalContactImpRepository.saveAll(list));
            }
            //案件信息
            if(!baseCaseSaveList.isEmpty()){
                importBaseCaseRepository.saveAll(baseCaseSaveList);
            }

            //删除临时数据
            BoolQueryBuilder boolQueryBuilder1=new BoolQueryBuilder();
            boolQueryBuilder1.must(termsQuery("id.keyword",caseDeleteList));
            DeleteByQueryRequestBuilder deleteByQueryRequestBuilder= DeleteByQueryAction.INSTANCE.
                    newRequestBuilder(elasticsearchTemplate.getClient());
            deleteByQueryRequestBuilder.source("base_case_all_import_excel_temp").filter(boolQueryBuilder1).refresh(true).execute();
            BoolQueryBuilder boolQueryBuilder2=new BoolQueryBuilder();
            boolQueryBuilder2.must(termsQuery("id.keyword",personalDeleteList));
            deleteByQueryRequestBuilder.source("base_personal_import_excel_temp").filter(boolQueryBuilder2).refresh(true).execute();
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            logger.info("确认第 {} 页数据失败",pageNo);
            return CompletableFuture.completedFuture(0);
        }finally {
            personalSaveList.clear();
            confirmTotals=baseCaseSaveList.size();
            baseCaseSaveList.clear();
            personalContactSaveList.clear();
            relationIdSets.clear();
            personalSets.clear();
        }
        logger.info("确认第 {} 页数据成功",pageNo);
        return CompletableFuture.completedFuture(confirmTotals);
    }

    private void createContactCell(Personal personalTmp, BasePersonalImportExcelTemp perObj, String phoneNo, String addr,
                                   PersonalContact personalContact,Snowflake snowflake) {
        if("公司".equals(perObj.getAddrType())){
            personalContact.getPersonalPerCalls().add(createPerCall(phoneNo,"公司",snowflake));
            personalContact.getPersonalPerAddrs().add(createPerAddr(addr,"公司",snowflake));
        }else if("家里".equals(perObj.getAddrType())){
            personalContact.getPersonalPerCalls().add(createPerCall(phoneNo,"家里",snowflake));
            personalContact.getPersonalPerAddrs().add(createPerAddr(addr,"家里",snowflake));
        }else if("手机".equals(perObj.getAddrType())){
            personalTmp.setSelfPhoneNo(phoneNo);
            personalContact.getPersonalPerCalls().add(createPerCall(phoneNo,"手机",snowflake));
            personalContact.getPersonalPerAddrs().add(createPerAddr(addr,"手机",snowflake));
        }else if("其它".equals(perObj.getAddrType())){
            personalContact.getPersonalPerCalls().add(createPerCall(phoneNo,"其他",snowflake));
            personalContact.getPersonalPerAddrs().add(createPerAddr(addr,"其他",snowflake));
        }else if("户籍".equals(perObj.getAddrType())){
            personalContact.getPersonalPerCalls().add(createPerCall(phoneNo,"户籍",snowflake));
            personalContact.getPersonalPerAddrs().add(createPerAddr(addr,"户籍",snowflake));
        }
    }

    private void createContactPF(Personal personalTmp, BasePersonalImportExcelTemp perObj, String phoneNo, String addr,
                                 PersonalContact personalContact,Snowflake snowflake) {
        if("公司".equals(perObj.getAddrType())){
            personalTmp.setEmployerAddr(addr);
            personalTmp.setEmployerPhoneNo(phoneNo);
            personalContact.getPersonalPerCalls().add(createPerCall(phoneNo,"公司",snowflake));
            personalContact.getPersonalPerAddrs().add(createPerAddr(addr,"公司",snowflake));
        }else if("家里".equals(perObj.getAddrType())){
            personalTmp.setHomeAddr(addr);
            personalTmp.setHomePhoneNo(phoneNo);
            personalContact.getPersonalPerCalls().add(createPerCall(phoneNo,"家里",snowflake));
            personalContact.getPersonalPerAddrs().add(createPerAddr(addr,"家里",snowflake));
        }else if("手机".equals(perObj.getAddrType())){
            personalTmp.setSelfPhoneNo(phoneNo);
            personalContact.getPersonalPerCalls().add(createPerCall(phoneNo,"手机",snowflake));
            personalContact.getPersonalPerAddrs().add(createPerAddr(addr,"手机",snowflake));
        }else if("其它".equals(perObj.getAddrType())){
            personalContact.getPersonalPerCalls().add(createPerCall(phoneNo,"其他",snowflake));
            personalContact.getPersonalPerAddrs().add(createPerAddr(addr,"其他",snowflake));
        }else if("户籍".equals(perObj.getAddrType())){
            personalTmp.setResidenceAddr(addr);
            personalContact.getPersonalPerCalls().add(createPerCall(phoneNo,"户籍",snowflake));
            personalContact.getPersonalPerAddrs().add(createPerAddr(addr,"户籍",snowflake));
        }
    }

    /**
     * 创建客户联系信息
     * @param basePersonal
     *
     */
    private void createPersonalContactInf(BasePersonalImportExcelTemp basePersonal,Snowflake snowflake,OperatorModel operator,String personalId,
                                          List<PersonalContact> personalContactSaveList){
        //本人
        PersonalContact personalContact= createContact(basePersonal.getPersonalName(),basePersonal.getCertificateNo(),basePersonal.getEmployerName(),0 ,
               "本人",snowflake,operator,personalId,personalContactSaveList);
        createPerContactObj(basePersonal.getSelfPhoneNo(),basePersonal.getHomePhoneNo(),basePersonal.getEmployerPhoneNo(),basePersonal.getEmployerAddr(),
                            basePersonal.getResidenceAddr(),basePersonal.getHomeAddr(),basePersonal.getEmailAddr(),basePersonal.getBillAddr(),
                            personalContact,snowflake);
        //配偶
        if(StringUtils.isBlank(basePersonal.getSpouseName())){
            PersonalContact personalContact1= createContact(basePersonal.getSpouseName(),basePersonal.getSpouseCertificateNo(),
                    basePersonal.getSpouseEmployerName(), 1 ,
                    "配偶",snowflake,operator,personalId,personalContactSaveList);
            createPerContactObj(basePersonal.getSpouseSelfPhoneNo(),basePersonal.getSpouseHomePhoneNo(),basePersonal.getSpouseEmployerPhoneNo(),
                    basePersonal.getSpouseEmployerAddr(), basePersonal.getSpouseResidenceAddr(),basePersonal.getSpouseHomeAddr(),
                    null,null, personalContact1,snowflake);
        }
        //直系亲属
        if(StringUtils.isBlank(basePersonal.getDirectName())){
            PersonalContact personalContact1= createContact(basePersonal.getDirectName(),basePersonal.getDirectCertificateNo(),
                    basePersonal.getDirectEmployerName(), 2 ,
                    StringUtils.isBlank(basePersonal.getDirectRelation()) ? "直系亲属" :basePersonal.getDirectRelation() ,
                    snowflake,operator,personalId,personalContactSaveList);
            createPerContactObj(basePersonal.getDirectSelfPhoneNo(),basePersonal.getDirectHomePhoneNo(),basePersonal.getDirectEmployerPhoneNo(),
                    basePersonal.getDirectEmployerAddr(), basePersonal.getDirectResidenceAddr(),basePersonal.getDirectHomeAddr(),
                    null,null, personalContact1,snowflake);
        }
        //联系人1
        if(!StringUtils.isBlank(basePersonal.getName1())){
            PersonalContact personalContact1= createContact(basePersonal.getName1(),basePersonal.getCertificateNo1(),basePersonal.getEmployerName1(),3 ,
                    basePersonal.getRelation1(),snowflake,operator,personalId,personalContactSaveList);
            createPerContactObj(basePersonal.getSelfPhoneNo1(),basePersonal.getHomePhoneNo1(),basePersonal.getEmployerPhoneNo1(),basePersonal.getEmployerAddr1(),
                    basePersonal.getResidenceAddr1(),basePersonal.getHomeAddr1(),null,null, personalContact1,snowflake);
        }
        //联系人2
        if(!StringUtils.isBlank(basePersonal.getName2())){
            PersonalContact personalContact2= createContact(basePersonal.getName2(),basePersonal.getCertificateNo2(),basePersonal.getEmployerName2(),4 ,
                    basePersonal.getRelation2(),snowflake,operator,personalId,personalContactSaveList);
            createPerContactObj(basePersonal.getSelfPhoneNo2(),basePersonal.getHomePhoneNo2(),basePersonal.getEmployerPhoneNo2(),basePersonal.getEmployerAddr2(),
                    basePersonal.getResidenceAddr2(),basePersonal.getHomeAddr2(),null,null, personalContact2,snowflake);
        }
        //联系人3
        if(!StringUtils.isBlank(basePersonal.getName3())){
            PersonalContact personalContact3= createContact(basePersonal.getName3(),basePersonal.getCertificateNo3(),basePersonal.getEmployerName3(),5 ,
                    basePersonal.getRelation3(),snowflake,operator,personalId,personalContactSaveList);
            createPerContactObj(basePersonal.getSelfPhoneNo3(),basePersonal.getHomePhoneNo3(),basePersonal.getEmployerPhoneNo3(),basePersonal.getEmployerAddr3(),
                    basePersonal.getResidenceAddr3(),basePersonal.getHomeAddr3(),null,null, personalContact3,snowflake);
        }
        //联系人4
        if(!StringUtils.isBlank(basePersonal.getName4())){
            PersonalContact personalContact4= createContact(basePersonal.getName4(),basePersonal.getCertificateNo4(),basePersonal.getEmployerName4(),6 ,
                    basePersonal.getRelation4(),snowflake,operator,personalId,personalContactSaveList);
            createPerContactObj(basePersonal.getSelfPhoneNo3(),basePersonal.getHomePhoneNo4(),basePersonal.getEmployerPhoneNo4(),basePersonal.getEmployerAddr4(),
                    basePersonal.getResidenceAddr4(),basePersonal.getHomeAddr4(),null,null, personalContact4,snowflake);
        }

        //联系人5
        if(!StringUtils.isBlank(basePersonal.getName5())){
            PersonalContact personalContact5= createContact(basePersonal.getName5(),basePersonal.getCertificateNo5(),basePersonal.getEmployerName5(),7 ,
                    basePersonal.getRelation5(),snowflake,operator,personalId,personalContactSaveList);
            createPerContactObj(basePersonal.getSelfPhoneNo5(),basePersonal.getHomePhoneNo5(),basePersonal.getEmployerPhoneNo5(),basePersonal.getEmployerAddr5(),
                    basePersonal.getResidenceAddr5(),basePersonal.getHomeAddr5(),null,null, personalContact5,snowflake);
        }

    }

    /**
     * 创建联系人信息
     * @param name
     * @param certificateNo
     * @param employerName
     * @param sort
     * @param relation
     * @param operator
     * @return
     */
    private PersonalContact  createContact(String name,String certificateNo,String employerName,int sort ,String relation,Snowflake snowflake,
                                           OperatorModel operator,String personalId,List<PersonalContact> personalContactSaveList){
        PersonalContact personalContact=new PersonalContact();
        personalContact.setId(String.valueOf(snowflake.next()));
        personalContact.setPersonalId(personalId);
        personalContact.setName(name);
        personalContact.setCertificateNo(certificateNo);
        personalContact.setEmployerName(employerName);
        personalContact.setSort(sort);
        personalContact.setRelation(relation);
        personalContact.setOperatorTime(ZWDateUtil.getNowDateTime());
        personalContact.setOperator(operator.getUsername());
        personalContactSaveList.add(personalContact);
        return personalContact;
    }

    /**
     *
     * @param selfPhoneNo
     * @param homePhoneNo
     * @param employerPhoneNo
     * @param employerAddr
     * @param residenceAddr
     * @param homeAddr
     * @param emailAddr
     * @param billAddr
     * @param personalContact
     * @param operator
     */
    private  void createPerContactObj(String selfPhoneNo,String homePhoneNo,String employerPhoneNo,String employerAddr,
                                      String residenceAddr,String homeAddr,String emailAddr,String billAddr,
                                      PersonalContact personalContact,Snowflake snowflake){
        //个人电话
        if(!StringUtils.isBlank(selfPhoneNo)){
            personalContact.getPersonalPerCalls().add(createPerCall(selfPhoneNo,"手机",snowflake));
        }
        //家庭电话
        if(!StringUtils.isBlank(homePhoneNo)){
            personalContact.getPersonalPerCalls().add(createPerCall(homePhoneNo,"家庭",snowflake));
        }
        //单位电话
        if(!StringUtils.isBlank(employerPhoneNo)){
            personalContact.getPersonalPerCalls().add(createPerCall(employerPhoneNo,"单位",snowflake));
        }
        //地址信息
        //单位
        if(!StringUtils.isBlank(employerAddr)){
            personalContact.getPersonalPerAddrs().add( createPerAddr(employerAddr,"单位",snowflake));
        }
        //户籍
        if(!StringUtils.isBlank(residenceAddr)){
            personalContact.getPersonalPerAddrs().add( createPerAddr(residenceAddr,"户籍",snowflake));
        }
        //住宅
        if(!StringUtils.isBlank(homeAddr)){
            personalContact.getPersonalPerAddrs().add( createPerAddr(homeAddr,"住宅",snowflake));
        }
        //邮件
        if(!StringUtils.isBlank(emailAddr)){
            personalContact.getPersonalPerAddrs().add( createPerAddr(emailAddr,"邮件",snowflake));
        }
        //邮寄
        if(!StringUtils.isBlank(billAddr)){
            personalContact.getPersonalPerAddrs().add( createPerAddr(billAddr,"邮寄",snowflake));
        }
    }
    /**
     * 创建联系人信息
     * @param phoneNo
     * @param phoneType
     * @return
     */
    private PersonalPerCall createPerCall(String phoneNo, String phoneType,Snowflake snowflake){
        PersonalPerCall perCall=new PersonalPerCall();
        perCall.setId(String.valueOf(snowflake.next()));
        perCall.setPhoneNo(phoneNo);
        perCall.setPhoneState("未知");
        perCall.setPhoneType(phoneType);
        perCall.setSource(Source.IMPORT);
        return perCall;
    }

    /**
     * 联系地址信息
     * @param detailAddr
     * @param addrType
     * @return
     */
    private PersonalPerAddr createPerAddr(String detailAddr,String addrType,Snowflake snowflake){
        PersonalPerAddr perAddr =new PersonalPerAddr();
        perAddr.setId(String.valueOf(snowflake.next()));
        perAddr.setAddressDetail(detailAddr);
        perAddr.setAddressType(addrType);
        perAddr.setSource(Source.IMPORT);
        perAddr.setAddressState("未知");
        return perAddr;
    }
}

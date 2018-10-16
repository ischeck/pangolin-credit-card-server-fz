package cn.fintecher.pangolin.service.repair.web;

import cn.fintecher.pangolin.entity.repair.*;
import cn.fintecher.pangolin.service.repair.model.RelationShipDataModel;
import cn.fintecher.pangolin.service.repair.model.SpecialTransferDataModel;
import cn.fintecher.pangolin.service.repair.model.response.ArchiveDataListResponse;
import cn.fintecher.pangolin.service.repair.model.response.ArchiveDataResponse;
import cn.fintecher.pangolin.service.repair.respository.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.IteratorUtils;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hanwannan on 2017/8/31.
 */
@RestController
@RequestMapping("/api/archiveData")
@Api(value = "档案资料", description = "档案资料")
public class ArchiveDataController {
    private final Logger log = LoggerFactory.getLogger(ArchiveDataController.class);

    @Autowired
    private KosekiDataRepository kosekiDataRepository;
    @Autowired
    private KosekiRemarkRepository kosekiRemarkRepository;
    @Autowired
    private SocialSecurityDataRepository socialSecurityDataRepository;
    @Autowired
    private SpecialTransferDataRepository specialTransferDataRepository;
    @Autowired
    private FamilyPlanningDataRepository familyPlanningDataRepository;
    @Autowired
    private RelationshipRepository relationshipRepository;
    @Autowired
    private CommunicationDataRepository communicationDataRepository;

    @GetMapping("/get")
    @ApiOperation(value = "获取档案资料", notes = "获取档案资料")
    public ResponseEntity get(String idNo) throws Exception {
        ArchiveDataResponse archiveDataResponse=new ArchiveDataResponse();
        archiveDataResponse.setKosekiData(getKosekiDataByIdNo(idNo).size()>0?getKosekiDataByIdNo(idNo).get(0):null);
        archiveDataResponse.setKosekiRemarkList(getKosekiRemarkByIdNo(idNo));
        archiveDataResponse.setSocialSecurityData(getSocialSecurityDataByIdNo(idNo).size()>0?getSocialSecurityDataByIdNo(idNo).get(0):null);
        archiveDataResponse.setSpecialTransferDataModel(getSpecialTransferDataByIdNo(idNo));
        archiveDataResponse.setFamilyPlanningDataModel(getFamilyPlanningDataModel(idNo));
        archiveDataResponse.setRelationshipList(getRelationshipSetByIdNo(idNo));
        return ResponseEntity.ok().body(archiveDataResponse);
    }

    @GetMapping("/detailList")
    @ApiOperation(value = "获取档案资料列表", notes = "获取档案资料")
    public ResponseEntity detailList(String idNo) throws Exception {
        List<ArchiveDataListResponse> archiveDataList=new ArrayList<>();
        //将户籍资料添加到archiveDataList
        List<KosekiData> kosekiDatas=getKosekiDataByIdNo(idNo);
        if(kosekiDatas.size()>0){
            kosekiDatas.forEach(kosekiData -> {
                ArchiveDataListResponse kosekiDataResponse=new ArchiveDataListResponse();
                kosekiDataResponse.setChannel("户籍资料");
                kosekiDataResponse.setName(kosekiData.getName());
                kosekiDataResponse.setIdNo(kosekiData.getIdNo());
                kosekiDataResponse.setImportDate(kosekiData.getImportDate());
                archiveDataList.add(kosekiDataResponse);
            });
        }
        //户籍备注查询
        List<KosekiRemark> kosekiRemarks = getKosekiRemarkByIdNo(idNo);
        if(kosekiRemarks.size()>0){
            kosekiRemarks.forEach(kosekiRemark -> {
                ArchiveDataListResponse kosekiRemarkResponse=new ArchiveDataListResponse();
                kosekiRemarkResponse.setChannel("户籍备注");
                kosekiRemarkResponse.setName(kosekiRemark.getName());
                kosekiRemarkResponse.setIdNo(kosekiRemark.getIdNo());
                kosekiRemarkResponse.setImportDate(kosekiRemark.getImportDate());
                archiveDataList.add(kosekiRemarkResponse);
            });
        }

        //计生资料添加到archiveDataList
        List<FamilyPlanningData> familyPlanningDatas=getFamilyPlanningDataByIdNo(idNo);
        if(familyPlanningDatas.size()>0){
            familyPlanningDatas.forEach(familyPlanningData -> {
                ArchiveDataListResponse familyPlanningDataResponse=new ArchiveDataListResponse();
                familyPlanningDataResponse.setChannel("计生资料");
                familyPlanningDataResponse.setName(familyPlanningData.getName());
                familyPlanningDataResponse.setIdNo(familyPlanningData.getIdNo());
                familyPlanningDataResponse.setImportDate(familyPlanningData.getImportDate());
                archiveDataList.add(familyPlanningDataResponse);
            });
        }

        //社保资料添加到archiveDataList
        List<SocialSecurityData> socialSecurityDatas=getSocialSecurityDataByIdNo(idNo);
        if(socialSecurityDatas.size()>0){
            socialSecurityDatas.forEach(socialSecurityData -> {
                ArchiveDataListResponse socialSecurityDataResponse=new ArchiveDataListResponse();
                socialSecurityDataResponse.setChannel("社保资料");
                socialSecurityDataResponse.setName(socialSecurityData.getName());
                socialSecurityDataResponse.setIdNo(socialSecurityData.getIdNo());
                socialSecurityDataResponse.setImportDate(socialSecurityData.getImportDate());
                archiveDataList.add(socialSecurityDataResponse);

            });
        }
        //通讯资料添加到archiveDataList
        List<CommunicationData> communicationDataList=getCommunicationDataListByIdNo(idNo);
        for (CommunicationData communicationData:communicationDataList) {
            ArchiveDataListResponse communicationDataResponse=new ArchiveDataListResponse();
            communicationDataResponse.setChannel(communicationData.getType());
            communicationDataResponse.setName(communicationData.getName());
            communicationDataResponse.setIdNo(communicationData.getIdNo());
            communicationDataResponse.setImportDate(communicationData.getImportDate());
            archiveDataList.add(communicationDataResponse);
        }

        //特调资料
        MatchQueryBuilder mqb= QueryBuilders.matchQuery("idNo",idNo);
        Iterable<SpecialTransferData> specialTransferDataIterable = specialTransferDataRepository.search(mqb);
        if(specialTransferDataIterable.iterator().hasNext()){
            List<SpecialTransferData> list = IteratorUtils.toList(specialTransferDataIterable.iterator());
            list.forEach(specialTransferData -> {
                ArchiveDataListResponse socialSecurityDataResponse=new ArchiveDataListResponse();
                socialSecurityDataResponse.setChannel("特调资料");
                socialSecurityDataResponse.setName(specialTransferData.getName());
                socialSecurityDataResponse.setIdNo(specialTransferData.getIdNo());
                socialSecurityDataResponse.setImportDate(specialTransferData.getImportDate());
                archiveDataList.add(socialSecurityDataResponse);
            });
        }

        //关联关系
        Iterable<Relationship> relationshipIterable = relationshipRepository.search(mqb);
        if(relationshipIterable.iterator().hasNext()){
            List<Relationship> relationShipDataModels = IteratorUtils.toList(relationshipIterable.iterator());
            relationShipDataModels.forEach(relationship -> {
                ArchiveDataListResponse socialSecurityDataResponse=new ArchiveDataListResponse();
                socialSecurityDataResponse.setChannel("特调资料");
                socialSecurityDataResponse.setName(relationship.getName());
                socialSecurityDataResponse.setIdNo(relationship.getIdNo());
                socialSecurityDataResponse.setImportDate(relationship.getImportDate());
                archiveDataList.add(socialSecurityDataResponse);
            });
        }
        return ResponseEntity.ok().body(archiveDataList);
    }

    /**
     * 根据idNo 获取户籍资料
     * @param idNo
     * @return
     * @throws Exception
     */
    private List<KosekiData> getKosekiDataByIdNo(String idNo) throws Exception {
        MatchQueryBuilder mqb= QueryBuilders.matchQuery("idNo",idNo);
        Iterable<KosekiData> kosekiDataIterable = kosekiDataRepository.search(mqb);
        if(kosekiDataIterable.iterator().hasNext()){
            List<KosekiData> list = IteratorUtils.toList(kosekiDataIterable.iterator());
            list.sort(Comparator.comparing(KosekiData::getImportDate));
            return list;
        }
        return null;
    }
    
    /**
     * 根据idNo 获取户籍备注
     * @param idNo
     * @return
     * @throws Exception
     */
    private List<KosekiRemark> getKosekiRemarkByIdNo(String idNo) throws Exception {
        MatchQueryBuilder mqb= QueryBuilders.matchQuery("idNo",idNo);
        Iterable<KosekiRemark> kosekiDataIterable = kosekiRemarkRepository.search(mqb);
        if(kosekiDataIterable.iterator().hasNext()){
            List<KosekiRemark> KosekiRemarkList = IteratorUtils.toList(kosekiDataIterable.iterator());
            KosekiRemarkList.sort(Comparator.comparing(KosekiRemark::getImportDate));
            return KosekiRemarkList;
        }
        return null;
    }

    /**
     * 根据idNo 获取社保资料
     * @param idNo
     * @return
     * @throws Exception
     */
    private List<SocialSecurityData> getSocialSecurityDataByIdNo(String idNo) throws Exception {
        MatchQueryBuilder mqb= QueryBuilders.matchQuery("idNo",idNo);
        Iterable<SocialSecurityData> socialSecurityDataIterable = socialSecurityDataRepository.search(mqb);
        List<SocialSecurityData> list = new ArrayList<>();
        if(socialSecurityDataIterable.iterator().hasNext()){
            list = IteratorUtils.toList(socialSecurityDataIterable.iterator());
            list.sort(Comparator.comparing(SocialSecurityData::getImportDate));
        }
        return list;
    }

    /**
     * 根据idNo 获取特调资料
     * @param idNo
     * @return
     * @throws Exception
     */
    private SpecialTransferDataModel getSpecialTransferDataByIdNo(String idNo) throws Exception {
        MatchQueryBuilder mqb= QueryBuilders.matchQuery("idNo",idNo);
        Iterable<SpecialTransferData> specialTransferDataIterable = specialTransferDataRepository.search(mqb);
        SpecialTransferDataModel model = new SpecialTransferDataModel();
        if(specialTransferDataIterable.iterator().hasNext()){
            SpecialTransferData next = specialTransferDataIterable.iterator().next();
            model.setRemark(next.getRemark());
            List<Credential> credentialSet = next.getCredentialSet();
            credentialSet.forEach(credential -> {
                model.getFileIds().add(credential.getFileId());
            });
        }
        return model;
    }

    /**
     * 根据idNo 获取计生资料
     * @param idNo
     * @return
     * @throws Exception
     */
    private List<FamilyPlanningData> getFamilyPlanningDataByIdNo(String idNo) throws Exception {
        MatchQueryBuilder mqb= QueryBuilders.matchQuery("idNo",idNo);
        Iterable<FamilyPlanningData> familyPlanningDataIterable = familyPlanningDataRepository.search(mqb);
        List<FamilyPlanningData> list = new ArrayList<>();
        if(familyPlanningDataIterable.iterator().hasNext()){
            list = IteratorUtils.toList(familyPlanningDataIterable.iterator());
        }
        return list;
    }

    /**
     * 根据idNo 获取计生资料的model
     * @param idNo
     * @return
     * @throws Exception
     */
    private SpecialTransferDataModel getFamilyPlanningDataModel(String idNo) throws Exception {
        MatchQueryBuilder mqb= QueryBuilders.matchQuery("idNo",idNo);
        Iterable<FamilyPlanningData> familyPlanningDataIterable = familyPlanningDataRepository.search(mqb);
        SpecialTransferDataModel model = new SpecialTransferDataModel();
        if(familyPlanningDataIterable.iterator().hasNext()){
            FamilyPlanningData next = familyPlanningDataIterable.iterator().next();
            model.setRemark(next.getRemark());
            List<Credential> credentialSet = next.getCredentialSet();
            if(credentialSet.size()>0){
                credentialSet.forEach(credential -> {
                    model.getFileIds().add(credential.getFileId());
                });
            }

            model.setFamilyPlanningArea(next.getFamilyPlanningArea());
        }
        return model;
    }

    /**
     * 根据idNo 获取关联关系集合
     * @param idNo
     * @return
     * @throws Exception
     */
    private List<RelationShipDataModel> getRelationshipSetByIdNo(String idNo) throws Exception {
        List<RelationShipDataModel> relationShipDataModels = new ArrayList<>();
        MatchQueryBuilder mqb= QueryBuilders.matchQuery("idNo",idNo);
        Iterable<Relationship> relationshipIterable = relationshipRepository.search(mqb);
        Iterator<Relationship> iterator = relationshipIterable.iterator();
        ConcurrentHashMap<String, CommunicationData> relationshipMap = getCommunicationDataList(idNo);
        //循环获取关联关系，同时将通讯资料中存在相同名字的信息合并再一起
        while (iterator.hasNext()){
            RelationShipDataModel model = new RelationShipDataModel();
            Relationship next = iterator.next();
            BeanUtils.copyProperties(next, model);
            if(relationshipMap.containsKey(next.getRelationPersonName())){
                CommunicationData communicationData = relationshipMap.get(next.getRelationPersonName());
                BeanUtils.copyProperties(communicationData, model);
                relationshipMap.remove(communicationData.getName());
            }
            relationShipDataModels.add(model);
        }
        //存放未匹配上的通讯信息
        if(relationshipMap.size()>0){
            for(Map.Entry<String, CommunicationData> set : relationshipMap.entrySet()){
                RelationShipDataModel model = new RelationShipDataModel();
                CommunicationData value = set.getValue();
                BeanUtils.copyProperties(value, model);
                relationShipDataModels.add(model);
            }
        }
        return relationShipDataModels;
    }

    /**
     * 根据idNo 获取通讯资料
     * @param idNo
     * @return
     * @throws Exception
     */
    private List<CommunicationData> getCommunicationDataListByIdNo(String idNo){
        MatchQueryBuilder mqb= QueryBuilders.matchQuery("idNo",idNo);
        Iterable<CommunicationData> relationshipIterable = communicationDataRepository.search(mqb);
        Iterator<CommunicationData> iterator=relationshipIterable.iterator();
        if (iterator.hasNext()){
            List<CommunicationData> relationshipList = IteratorUtils.toList(iterator);
            relationshipList.sort(Comparator.comparing(CommunicationData::getImportDate));
            return relationshipList;
        }
        return null;
    }

    /**
     * 根据idNo 获取通讯资料
     * @param idNo
     * @return
     * @throws Exception
     */
    private ConcurrentHashMap<String, CommunicationData> getCommunicationDataList(String idNo){
        MatchQueryBuilder mqb= QueryBuilders.matchQuery("idNo",idNo);
        Iterable<CommunicationData> relationshipIterable = communicationDataRepository.search(mqb);
        Iterator<CommunicationData> iterator=relationshipIterable.iterator();
        ConcurrentHashMap<String, CommunicationData> relationshipMap=new ConcurrentHashMap<>();
        while (iterator.hasNext()){
            CommunicationData next = iterator.next();
            relationshipMap.put(next.getName(), next);
        }
        return relationshipMap;
    }

}

package cn.fintecher.pangolin.service.management.service;

import cn.fintecher.pangolin.common.annotation.ExcelAnno;
import cn.fintecher.pangolin.common.enums.ConfigState;
import cn.fintecher.pangolin.common.enums.RelationList;
import cn.fintecher.pangolin.common.model.CaseFollowRecordMatchModel;
import cn.fintecher.pangolin.common.model.response.CaseFollowRecordMatchResponse;
import cn.fintecher.pangolin.common.utils.ZWStringUtils;
import cn.fintecher.pangolin.entity.managentment.ContactResult;
import cn.fintecher.pangolin.entity.managentment.QContactResult;
import cn.fintecher.pangolin.service.management.repository.ContactResultRepository;
import org.apache.commons.collections4.IteratorUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author:huyanmin
 * @Desc:
 * @Date:Create 2018/8/22
 */
@Service("custConfigService")
public class CustConfigService {

    Logger logger = LoggerFactory.getLogger(CustConfigService.class);

    @Autowired
    ContactResultRepository contactResultRepository;
    @Autowired
    ModelMapper modelMapper;
    /**
     * 获取指定类的属性
     *
     * @param objClassList
     * @return
     */
    public List<CaseFollowRecordMatchModel> getFollowRecordModel(List<Class<?>> objClassList) {
        List<CaseFollowRecordMatchModel> matchModels = new ArrayList<>();
        logger.debug("获取注解字段");
        for (Class<?> objClass : objClassList) {
            //获取类中所有的字段
            Field[] fields = objClass.getDeclaredFields();
            for (Field field : fields) {
                //获取标记了ExcelAnno的注解字段
                if (field.isAnnotationPresent(ExcelAnno.class)) {
                    if(Objects.equals(field.getName(),"addrType")
                        || Objects.equals(field.getName(),"addrStatus")
                        || Objects.equals(field.getName(),"detail")){
                            continue;
                    }
                    ExcelAnno f = field.getAnnotation(ExcelAnno.class);
                    CaseFollowRecordMatchModel matchModel = new CaseFollowRecordMatchModel();
                    matchModel.setAttribute(field.getName());
                    matchModel.setName(f.cellName());
                    matchModel.setPropertyType(f.fieldType().name());
                    matchModel.setIsMustInput(f.fieldInput().name());
                    matchModels.add(matchModel);
                }
            }
        }
        return matchModels;
    }

    /***
     * 递归获取催收字段级联结果
     * @param allConfig
     * @param returnSon
     * @param parentId
     * @return
     */
    public List<CaseFollowRecordMatchResponse> getSonConfig(String principalId, List<CaseFollowRecordMatchResponse> allConfig, List<CaseFollowRecordMatchResponse> returnSon, List<CaseFollowRecordMatchResponse> parentId) {

        List<CaseFollowRecordMatchResponse> newSon = new ArrayList<>();
        if (parentId.size() == 0) {
            return returnSon;
        }
        for (int i = 0; i < parentId.size(); i++) {
            for (CaseFollowRecordMatchResponse response : allConfig) {
                if (response.getPid().equals(parentId.get(i).getId())) {
                    returnSon.add(response);
                    newSon.add(response);
                }
                if(RelationList.PROMISED_PAYMENT.getRemark().equals(response.getName()) || RelationList.PROMISED_SUB_PAYMENT.getRemark().equals(response.getName())
                        || RelationList.HOLDER_PROMISED__PAYMENT.getRemark().equals(response.getName()) || RelationList.PTP.getRemark().equals(response.getName())){
                    List<String> listName = new ArrayList<>();
                    listName.add(RelationList.PROMISED_AMOUNT.getRemark());
                    listName.add(RelationList.PROMISED_DATE.getRemark());
                    List<String> relationList = getRelationList(listName, principalId);
                    response.setRelationList(relationList);
                }
                if(RelationList.CP.getRemark().equals(response.getName()) || RelationList.HAS_PAYMENT.getRemark().equals(response.getName())){
                    List<String> listName = new ArrayList<>();
                    listName.add(RelationList.HAS_AMOUNT.getRemark());
                    listName.add(RelationList.HAS_DATE.getRemark());
                    List<String> relationList = getRelationList(listName, principalId);
                    response.setRelationList(relationList);
                }
            }
        }
        getSonConfig(principalId, allConfig, returnSon, newSon);
        return returnSon;
    }
    
    private List<String> getRelationList(List<String> listName, String principalId){
        Iterable<ContactResult> all = contactResultRepository.findAll(QContactResult.contactResult.name.in(listName)
                .and(QContactResult.contactResult.principalId.eq(principalId).and(QContactResult.contactResult.configState.eq(ConfigState.ENABLED))));
        List<String> list = new ArrayList<>();
        if(all.iterator().hasNext()){
            List<ContactResult> contactResults = IteratorUtils.toList(all.iterator());
            contactResults.forEach(contactResult -> list.add(contactResult.getId()));
        }
        return list;
    }

    public List<CaseFollowRecordMatchResponse> getFollowRecordFields(Iterable<ContactResult> all, String principalId,String type){
        List<CaseFollowRecordMatchResponse> responses = new ArrayList<>();
        if (all.iterator().hasNext()) {
            Type listType = new TypeToken<List<CaseFollowRecordMatchResponse>>() {
            }.getType();
            List<ContactResult> list = IteratorUtils.toList(all.iterator());
            responses = modelMapper.map(list, listType);
            List<CaseFollowRecordMatchResponse> childListRemove = new ArrayList<>();
            for (CaseFollowRecordMatchResponse response : responses) {
                if(ZWStringUtils.isEmpty(type) || !Objects.equals(type,"addr")) {
                    if (Objects.equals(response.getAttribute(), "detail")) {
                        childListRemove.add(response);
                        continue;
                    }
                    if (Objects.equals(response.getAttribute(), "addrStatus")
                            || Objects.equals(response.getAttribute(), "addrType")) {
                        List<CaseFollowRecordMatchResponse> childList = new ArrayList<>();
                        List<CaseFollowRecordMatchResponse> parentList = new ArrayList<>();
                        parentList.add(response);
                        childList = getSonConfig(principalId, responses, childList, parentList);
                        childListRemove.addAll(childList);
                        childListRemove.add(response);
                        continue;
                    }
                }
                if (Objects.equals(response.getPropertyType(),ExcelAnno.FieldType.SELECT.name())) {
                    List<CaseFollowRecordMatchResponse> childList = new ArrayList<>();
                    List<CaseFollowRecordMatchResponse> parentList = new ArrayList<>();
                    parentList.add(response);
                    childList = getSonConfig(principalId, responses, childList, parentList);
                    response.setChildList(childList);
                    childListRemove.addAll(childList);
                }
            }
            responses.removeAll(childListRemove);
        }
        return responses;
    }



}

package cn.fintecher.pangolin.service.management.service;


import cn.fintecher.pangolin.common.enums.OrganizationType;
import cn.fintecher.pangolin.entity.managentment.Operator;
import cn.fintecher.pangolin.entity.managentment.Organization;
import cn.fintecher.pangolin.entity.managentment.QOperator;
import cn.fintecher.pangolin.entity.managentment.QOrganization;
import cn.fintecher.pangolin.service.management.repository.OperatorRepository;
import cn.fintecher.pangolin.service.management.repository.OrganizationRepository;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by huyanmin on 2018/07/23.
 */
@Service("organizationService")
public class OrganizationService {

    final Logger log = LoggerFactory.getLogger(OrganizationService.class);

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OperatorRepository operatorRepository;

    /***
     * 获取结构子机构ID
     * @param orgId
     * @return
     */
    public Set<String> getOrganizationID(String orgId, List<Organization> all ){

        List<String> parentOrSonId = new ArrayList<>();
        Set<String> returnParentOrSon = new LinkedHashSet<>();
        parentOrSonId.add(orgId);
        //递归获取子结构
        returnParentOrSon = getSonId(all, returnParentOrSon, parentOrSonId);
        returnParentOrSon.add(orgId);

        return returnParentOrSon;
    }


    /***
     * 获取组织机构
     * @param returnOrgId
     * @return
     */
    public List<Organization> getOrganization(Set<String> returnOrgId){
        List<Organization> returnSon = new ArrayList<>();
        Iterable<Organization> all1 = organizationRepository.findAll(QOrganization.organization.id.in(returnOrgId));
        if(all1.iterator().hasNext()){
            returnSon = IteratorUtils.toList(all1.iterator());
        }
        return returnSon;
    }

    /***
     * 递归获取父结构
     * @param allOrg
     * @param returnParent
     * @param parentId
     * @return
     */
    public Set<String> getParentId(List<Organization> allOrg,  Set<String> returnParent,List<String> parentId){

        List<String> newParent = new ArrayList<>();
        if(parentId.size()==0){
            return returnParent;
        }
        for(int i =0; i<parentId.size(); i++){
            for(Organization org : allOrg){
                if(org.getId().equals(parentId.get(i))){
                    returnParent.add(org.getId());
                    newParent.add(org.getParent());
                }
            }
        }
        getParentId(allOrg, returnParent, newParent);
        return returnParent;
    }

    /***
     * 递归获取子结构
     * @param allOrg
     * @param returnSon
     * @param sonId
     * @return
     */
    private Set<String> getSonId( List<Organization> allOrg,  Set<String> returnSon, List<String> sonId){

        List<String> newSont = new ArrayList<>();
        if(sonId.size()==0){
            return returnSon;
        }
        for(int i =0; i<sonId.size(); i++){
            for(Organization org : allOrg){
                if(org.getParent().equals(sonId.get(i))){
                    returnSon.add(org.getId());
                    newSont.add(org.getId());
                }
            }
        }
        getSonId(allOrg, returnSon, newSont);
        return returnSon;
    }

    /***
     * 获取结构子机构ID
     * 审批时需要根据申请人的机构获取分公司的机构ID，再根据分公司的机构ID获取分公司下的所有组
     * @param orgId
     * @return
     */
    public Set<String> getParentOrganizationID(String orgId, List<Organization> all){

        List<String> parentOrSonId = new ArrayList<>();
        Set<String> returnParentId = new LinkedHashSet<>();
        parentOrSonId.add(orgId);
        //递归获取父结构
        returnParentId = getParentId(all, returnParentId, parentOrSonId);
        Set<String> returnParents = new LinkedHashSet<>(returnParentId);
        return returnParents;
    }

    /***
     * 审批时需要根据申请人的机构获取分公司的机构ID，再根据分公司的机构ID获取分公司下的所有组
     * @param orgId
     * @return
     */
    public Set<String> getParentOrg(String orgId){
        List<Organization> all = organizationRepository.findAll();
        Set<String> parentList = getParentOrganizationID(orgId, all);
        Set<String> returnList = new LinkedHashSet<>();
        List<Organization> organizations = getOrganization(parentList);
        String branch = null;
        if(organizations.size()>0){
            for(Organization organization:organizations){
                if(organization.getType().equals(OrganizationType.BRANCH)){
                    branch  = organization.getId();
                }
            }
        }
        if(Objects.nonNull(branch)){
            returnList =  getOrganizationID(branch, all);
        }
        returnList.add(orgId);
        return returnList;
    }


    public List<Operator> getAllOperator(String orgId){
        List<Operator> all = new ArrayList<>();
        List<Organization> organizationList = IterableUtils.toList(organizationRepository.findAll(QOrganization.organization.parent.eq(orgId)));
        all.addAll(IterableUtils.toList(operatorRepository.findAll(QOperator.operator.organization.eq(orgId))));
        for(Organization organization : organizationList){
            all.addAll(getAllOperator(organization.getId()));
        }
        return all;
    }
}
package cn.fintecher.pangolin.service.dataimp.client;


import cn.fintecher.pangolin.common.model.PrincipalModel;
import cn.fintecher.pangolin.entity.managentment.CustConfig;
import cn.fintecher.pangolin.entity.managentment.Organization;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

/**
 * Created by ChenChang on 2017/8/28.
 */
@FeignClient("management-service")
public interface OrganizationClient {
    @RequestMapping(method = RequestMethod.GET, value = "/api/organization/{id}")
    ResponseEntity<Organization> get(@PathVariable("id") String id);

    @RequestMapping(method = RequestMethod.GET, value ="/api/organization/getByIds/{ids}")
     ResponseEntity<List<Organization>> getByIds(@PathVariable("ids") Set<String> ids);

    @RequestMapping(method = RequestMethod.GET, value = "/api/organization/findAllBranch/{id}")
    ResponseEntity<List<Organization>> findOrgIdsByLevelLess(@PathVariable("id") String id);

    @RequestMapping(method = RequestMethod.GET, value = "/api/custConfig/getCaseStateByPrin")
    ResponseEntity<List<CustConfig>> getAllCustConfig();

    @RequestMapping(method = RequestMethod.GET, value = "/api/principal/findAll")
    ResponseEntity<List<PrincipalModel>> findAllPrincipal();

    @RequestMapping(method = RequestMethod.GET, value = "/api/organization/getUserNumByOrgId/{orgId}")
    ResponseEntity<Integer> getUserNumByOrgId(@PathVariable("orgId") String orgId);

}

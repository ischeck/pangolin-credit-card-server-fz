package cn.fintecher.pangolin.service.dataimp.client;

import cn.fintecher.pangolin.common.model.response.LoginResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient("management-service")
public interface PeriodTransformClient {
    @RequestMapping(method = RequestMethod.GET, value = "/api/periodTransform/getOneTransformTemplate")
    ResponseEntity<Map<String, String>> getOneTransformTemplate(@RequestParam String principalId);
}

package cn.fintecher.pangolin.service.common.client;


import cn.fintecher.pangolin.common.model.response.LoginResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by ChenChang on 2017/8/28.
 */
@FeignClient("management-service")
public interface OperatorCommonClient {
    @RequestMapping(method = RequestMethod.GET, value = "/api/operators/getUserByToken/{token}")
    ResponseEntity<LoginResponse> getUserByToken(@PathVariable("token") String token);

}

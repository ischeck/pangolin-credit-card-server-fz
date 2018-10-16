package cn.fintecher.pangolin.service.domain.web;

import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.entity.domain.BaseCase;
import cn.fintecher.pangolin.service.domain.model.response.CaseDetailResponse;
import cn.fintecher.pangolin.service.domain.respository.BaseCaseRepository;
import cn.fintecher.pangolin.service.domain.respository.PersonalContactRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by huyanmin on 2018/7/11.
 */
@RestController
@RequestMapping("/api/caseDetail")
@Api(value = "案件详情", description = "案件详情")
public class CaseDetailController {

    private final Logger log = LoggerFactory.getLogger(CaseDetailController.class);

    @Autowired
    private BaseCaseRepository baseCaseRepository;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    PersonalContactRepository personalContactRepository;

    @GetMapping("/caseDetail/{id}")
    @ApiOperation(value = "查询案件详情", notes = "查询案件详情")
    public ResponseEntity<CaseDetailResponse> getCaseDetail(@PathVariable String id) {
        log.info("查询案件详情---start---");
        CaseDetailResponse caseDetailResponse = new CaseDetailResponse();
        Optional<BaseCase> byId = baseCaseRepository.findById(id);
        byId.orElseThrow(() -> new BadRequestException(null, "caseDetail", "caseDetail.is.not.exist"));
        BaseCase baseCase = byId.get();
        if (Objects.nonNull(baseCase)) {
            BeanUtils.copyProperties(baseCase, caseDetailResponse);
        }
        log.info("查询案件详情---end---");
        return new ResponseEntity<>(caseDetailResponse, HttpStatus.OK);
    }

}

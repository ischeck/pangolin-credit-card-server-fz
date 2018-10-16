package cn.fintecher.pangolin.service.dataimp.web;

import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.response.LoginResponse;
import cn.fintecher.pangolin.common.utils.BeanUtils;
import cn.fintecher.pangolin.entity.domain.CollectionCaseStrategyConfig;
import cn.fintecher.pangolin.entity.managentment.Organization;
import cn.fintecher.pangolin.entity.managentment.Principal;
import cn.fintecher.pangolin.service.dataimp.client.OperatorClient;
import cn.fintecher.pangolin.service.dataimp.client.OrganizationClient;
import cn.fintecher.pangolin.service.dataimp.model.request.CollectionCaseStrategyConfigModel;
import cn.fintecher.pangolin.service.dataimp.model.request.CollectionCaseStrategyConfigSearchRequest;
import cn.fintecher.pangolin.service.dataimp.model.response.CollectionCaseStrategyConfigListResponse;
import cn.fintecher.pangolin.service.dataimp.repository.CollectionCaseStrategyConfigRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by ChenChang on 2017/8/4.
 */
@RestController
@RequestMapping("/api/collectionCaseStrategyConfig")
@Api(value = "策略分案配置", description = "策略分案配置")
public class CollectionCaseStrategyConfigController {
    private final Logger log = LoggerFactory.getLogger(CollectionCaseStrategyConfigController.class);

    @Autowired
    private CollectionCaseStrategyConfigRepository collectionCaseStrategyConfigRepository;
    @Autowired
    private OrganizationClient organizationClient;
    @Autowired
    private OperatorClient operatorClient;

    @GetMapping("/searchStrategy")
    @ApiOperation(value = "策略查询", notes = "策略查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<CollectionCaseStrategyConfigListResponse>> searchStrategy(Pageable pageable, CollectionCaseStrategyConfigSearchRequest request) {

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(request.generateQueryBuilder()).build();
        log.debug("search Activity : {} query :{}", request.toString(), searchQuery.getQuery().toString());
        Page<CollectionCaseStrategyConfig> searchPageResults = collectionCaseStrategyConfigRepository.search(searchQuery);
        Page<CollectionCaseStrategyConfigListResponse> responses = searchPageResults.map(collectionCaseStrategyConfig -> {
            CollectionCaseStrategyConfigListResponse response = new CollectionCaseStrategyConfigListResponse();
            BeanUtils.copyProperties(collectionCaseStrategyConfig, response);
            response.setOrganization(collectionCaseStrategyConfig.getOrganization().getId());
            response.setOrganizationName(collectionCaseStrategyConfig.getOrganization().getName());
            return response;
        });
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PostMapping("/addStrategy")
    @ApiOperation(value = "新增", notes = "新增")
    public ResponseEntity<CollectionCaseStrategyConfigModel> addStrategy(@RequestBody CollectionCaseStrategyConfigModel request,
                                                                         @RequestHeader(value = "X-UserToken") String token) throws JSONException {
        if (Objects.nonNull(request.getId())) {
            throw new BadRequestException(null, "id is exist", "id.is.exist");
        }
        log.debug("search assistCase apply {}", request);
        LoginResponse userByToken = operatorClient.getUserByToken(token).getBody();
        Organization organization = organizationClient.get(request.getOrganization()).getBody();
        CollectionCaseStrategyConfig collectionCaseStrategyConfig = new CollectionCaseStrategyConfig();
        BeanUtils.copyProperties(request, collectionCaseStrategyConfig);
        collectionCaseStrategyConfig.setOrganization(organization);
        collectionCaseStrategyConfig.setOperatorName(userByToken.getUser().getFullName());
        collectionCaseStrategyConfig.setCreateTime(new Date());
        collectionCaseStrategyConfig.analysisFormula(collectionCaseStrategyConfig.getFormulaJson(), new StringBuilder());
        collectionCaseStrategyConfig = collectionCaseStrategyConfigRepository.save(collectionCaseStrategyConfig);
        request.setId(collectionCaseStrategyConfig.getId());
        return new ResponseEntity<>(request, HttpStatus.OK);
    }

    @PutMapping("/editStrategy")
    @ApiOperation(value = "修改", notes = "修改")
    public ResponseEntity<CollectionCaseStrategyConfigModel> editStrategy(@RequestBody CollectionCaseStrategyConfigModel request) throws JSONException {
        if (Objects.isNull(request.getId())) {
            throw new BadRequestException(null, "id is not exist", "id.is.not.exist");
        }
        log.debug("search assistCase apply {}", request);
        Optional<CollectionCaseStrategyConfig> byId = collectionCaseStrategyConfigRepository.findById(request.getId());
        byId.orElseThrow(()->new BadRequestException(null, "strategy", "strategy.is.not.exist"));
        Organization organization = organizationClient.get(request.getOrganization()).getBody();
        CollectionCaseStrategyConfig collectionCaseStrategyConfig = byId.get();
        BeanUtils.copyProperties(request, collectionCaseStrategyConfig);
        collectionCaseStrategyConfig.setOrganization(organization);
        collectionCaseStrategyConfig.analysisFormula(collectionCaseStrategyConfig.getFormulaJson(), new StringBuilder());
        collectionCaseStrategyConfigRepository.save(collectionCaseStrategyConfig);
        return new ResponseEntity<>(request, HttpStatus.OK);
    }
}

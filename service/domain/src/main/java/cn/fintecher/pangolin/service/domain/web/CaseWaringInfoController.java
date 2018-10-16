package cn.fintecher.pangolin.service.domain.web;

import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.domain.CaseWarningInfo;
import cn.fintecher.pangolin.entity.domain.PublicCase;
import cn.fintecher.pangolin.service.domain.model.request.CaseWarningInfoRequest;
import cn.fintecher.pangolin.service.domain.model.request.PublicCaseSearchRequest;
import cn.fintecher.pangolin.service.domain.model.response.PublicCaseSearchResponse;
import cn.fintecher.pangolin.service.domain.respository.BaseCaseRepository;
import cn.fintecher.pangolin.service.domain.respository.CaseWarningInfoRepository;
import cn.fintecher.pangolin.service.domain.respository.PublicCaseRepository;
import cn.fintecher.pangolin.service.domain.service.DomainBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.IteratorUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

/**
 * @Author:胡艳敏
 * @Desc: 警告信息查询
 * @Date:Create 2018/9/14
 */
@RestController
@RequestMapping("/api/caseWaringInfoController")
@Api(value = "警告信息查询", description = "警告信息查询")
public class CaseWaringInfoController {

    Logger logger = LoggerFactory.getLogger(CaseWaringInfoController.class);
    @Autowired
    private CaseWarningInfoRepository caseWarningInfoRepository;

    @Autowired
    private DomainBaseService domainBaseService;

    @GetMapping("/queryCaseWarningInfo")
    @ApiOperation(value = "查询警告信息", notes = "查询警告信息")
    public ResponseEntity<List<String>> queryPublicCase(@RequestParam String caseId) {
        logger.debug("查询警告信息 查询条件{}", caseId);

        List<String> responseWaring = new ArrayList<>();
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        builder.must(matchPhraseQuery("caseId", caseId));
        Iterable<CaseWarningInfo> search = caseWarningInfoRepository.search(builder);
        if(search.iterator().hasNext()){
            List<CaseWarningInfo> caseWarningInfos = IteratorUtils.toList(search.iterator());
            caseWarningInfos.forEach(caseWarningInfo -> responseWaring.add(caseWarningInfo.getMsg()));
        }
        return new ResponseEntity<>(responseWaring, HttpStatus.OK);
    }

    @PostMapping("/insertCaseWarningInfo")
    @ApiOperation(value = "添加警告信息", notes = "添加警告信息")
    public ResponseEntity insertCaseWarningInfo(@RequestBody CaseWarningInfoRequest request,
                                                @RequestHeader(value = "X-UserToken") String token) {
        logger.debug("添加警告信息", request);
        CaseWarningInfo caseWarningInfo = new CaseWarningInfo();
        BeanUtils.copyProperties(request, caseWarningInfo);
        OperatorModel operator = domainBaseService.getOperator(token);
        caseWarningInfo.setOperator(operator.getId());
        caseWarningInfo.setOperatorTime(ZWDateUtil.getNowDateTime());
        caseWarningInfoRepository.save(caseWarningInfo);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}

package cn.fintecher.pangolin.service.domain.web;

import cn.fintecher.pangolin.entity.domain.ChinaFiveLevelArea;
import cn.fintecher.pangolin.service.domain.model.request.ChinaFiveLevelAreaSearchRequest;
import cn.fintecher.pangolin.service.domain.respository.ChinaFiveLevelAreaRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by ChenChang on 2017/8/4.
 */
@RestController
@RequestMapping("/api/chinaFiveLevelArea")
@Api(value = "个人信息", description = "个人信息")
public class ChinaFiveLevelAreaController {
    private final Logger log = LoggerFactory.getLogger(ChinaFiveLevelAreaController.class);
    @Autowired
    private ChinaFiveLevelAreaRepository personalRepository;

    @GetMapping("/search")
    @ApiOperation(value = "查询", notes = "查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<ChinaFiveLevelArea>> search(Pageable pageable, ChinaFiveLevelAreaSearchRequest request) {

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(request.generateQueryBuilder()).build();
        log.debug("search Activity : {} query :{}", request.toString(), searchQuery.getQuery().toString());
        Page<ChinaFiveLevelArea> searchPageResults = personalRepository.search(searchQuery);
        return new ResponseEntity<>(searchPageResults, HttpStatus.OK);
    }

}

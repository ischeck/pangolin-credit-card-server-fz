package cn.fintecher.pangolin.service.repair.web;

import cn.fintecher.pangolin.common.utils.BeanUtils;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.repair.SocialSecurityData;
import cn.fintecher.pangolin.service.repair.model.request.CreateSocialSecurityDataRequest;
import cn.fintecher.pangolin.service.repair.model.request.SocialSecurityDataSearchRequest;
import cn.fintecher.pangolin.service.repair.model.request.UpdateSocialSecurityDataRequest;
import cn.fintecher.pangolin.service.repair.respository.SocialSecurityDataRepository;
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
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by hanwannan on 2017/8/27.
 */
@RestController
@RequestMapping("/api/socialSecurityData")
@Api(value = "社保资料", description = "社保资料")
public class SocialSecurityDataController {
    private final Logger log = LoggerFactory.getLogger(SocialSecurityDataController.class);

    @Autowired
    private SocialSecurityDataRepository socialSecurityDataRepository;

    @GetMapping("/search")
    @ApiOperation(value = "查询社保资料", notes = "查询社保资料")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<SocialSecurityData>> search(Pageable pageable, SocialSecurityDataSearchRequest request) {

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(request.generateQueryBuilder()).build();
        log.debug("search social security data : {} query :{}", request.toString(), searchQuery.getQuery().toString());
        Page<SocialSecurityData> searchPageResults = socialSecurityDataRepository.search(searchQuery);
        return new ResponseEntity<>(searchPageResults, HttpStatus.OK);
    }

    @GetMapping("/get")
    @ApiOperation(value = "获取社保资料", notes = "获取社保资料")
    public ResponseEntity get(String id) throws Exception {
        Optional<SocialSecurityData> byId = socialSecurityDataRepository.findById(id);
        SocialSecurityData socialSecurityData = byId.get();
        return ResponseEntity.ok().body(socialSecurityData);
    }

    @PostMapping("/insert")
    @ApiOperation(value = "创建社保资料", notes = "创建社保资料")
    public ResponseEntity insert(@RequestBody CreateSocialSecurityDataRequest socialSecurityDataRequest) throws Exception {
        log.debug("Create social security data {}", socialSecurityDataRequest);
        SocialSecurityData socialSecurityData = new SocialSecurityData();
        BeanUtils.copyProperties(socialSecurityDataRequest, socialSecurityData);
        socialSecurityData.setImportDate(ZWDateUtil.getNowDateTime());
        socialSecurityDataRepository.save(socialSecurityData);
        return ResponseEntity.ok().body(socialSecurityData);
    }

    @PostMapping("/update")
    @ApiOperation(value = "修改社保资料", notes = "修改社保资料")
    public ResponseEntity update(@RequestBody UpdateSocialSecurityDataRequest socialSecurityDataRequest) throws Exception {

        Optional<SocialSecurityData> byId = socialSecurityDataRepository.findById(socialSecurityDataRequest.getId());
        SocialSecurityData socialSecurityData = byId.get();
        BeanUtils.copyProperties(socialSecurityDataRequest, socialSecurityData);
        socialSecurityDataRepository.save(socialSecurityData);
        return ResponseEntity.ok().body(socialSecurityData);
    }

    @GetMapping("/delete")
    @ApiOperation(value = "删除社保资料", notes = "删除社保资料")
    public ResponseEntity delete(String id){
        socialSecurityDataRepository.deleteById(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/deleteBatch")
    @ApiOperation(value = "批量删除社保资料", notes = "批量删除社保资料")
    public ResponseEntity delete(@RequestParam("id")String[] ids){
        ArrayList<SocialSecurityData> socialSecurityDataList=new ArrayList<>();
        SocialSecurityData socialSecurityData=null;
        for(String id:ids){
            socialSecurityData=new SocialSecurityData();
            socialSecurityData.setId(id);
            socialSecurityDataList.add(socialSecurityData);
        }
        socialSecurityDataRepository.deleteAll(socialSecurityDataList);
        return ResponseEntity.ok().body(null);
    }

}

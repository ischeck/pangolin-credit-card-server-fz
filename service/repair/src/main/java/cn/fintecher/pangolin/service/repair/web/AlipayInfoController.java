package cn.fintecher.pangolin.service.repair.web;

import cn.fintecher.pangolin.common.utils.BeanUtils;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.repair.AlipayInfo;
import cn.fintecher.pangolin.service.repair.model.request.AlipayInfoSearchRequest;
import cn.fintecher.pangolin.service.repair.model.request.CreateAlipayInfoRequest;
import cn.fintecher.pangolin.service.repair.model.request.UpdateAlipayInfoRequest;
import cn.fintecher.pangolin.service.repair.respository.AlipayInfoRepository;
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
@RequestMapping("/api/alipayInfo")
@Api(value = "支付宝信息", description = "支付宝信息")
public class AlipayInfoController {
    private final Logger log = LoggerFactory.getLogger(AlipayInfoController.class);

    @Autowired
    private AlipayInfoRepository alipayInfoRepository;

    @GetMapping("/search")
    @ApiOperation(value = "查询支付宝信息", notes = "查询支付宝信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<AlipayInfo>> search(Pageable pageable, AlipayInfoSearchRequest request) {

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(request.generateQueryBuilder()).build();
        log.debug("search alipay info : {} query :{}", request.toString(), searchQuery.getQuery().toString());
        Page<AlipayInfo> searchPageResults = alipayInfoRepository.search(searchQuery);
        return new ResponseEntity<>(searchPageResults, HttpStatus.OK);
    }

    @GetMapping("/get")
    @ApiOperation(value = "获取支付宝信息", notes = "获取支付宝信息")
    public ResponseEntity get(String id) throws Exception {
        Optional<AlipayInfo> byId = alipayInfoRepository.findById(id);
        AlipayInfo alipayInfo = byId.get();
        return ResponseEntity.ok().body(alipayInfo);
    }

    @PostMapping("/insert")
    @ApiOperation(value = "创建支付宝信息", notes = "创建支付宝信息")
    public ResponseEntity insert(@RequestBody CreateAlipayInfoRequest alipayInfoRequest) throws Exception {
        log.debug("Create alipay info {}", alipayInfoRequest);
        AlipayInfo alipayInfo = new AlipayInfo();
        BeanUtils.copyProperties(alipayInfoRequest, alipayInfo);
        alipayInfo.setImportDate(ZWDateUtil.getNowDateTime());
        alipayInfoRepository.save(alipayInfo);
        return ResponseEntity.ok().body(alipayInfo);
    }

    @PostMapping("/update")
    @ApiOperation(value = "修改支付宝信息", notes = "修改支付宝信息")
    public ResponseEntity update(@RequestBody UpdateAlipayInfoRequest alipayInfoRequest) throws Exception {

        Optional<AlipayInfo> byId = alipayInfoRepository.findById(alipayInfoRequest.getId());
        AlipayInfo alipayInfo = byId.get();
        BeanUtils.copyProperties(alipayInfoRequest, alipayInfo);
        alipayInfoRepository.save(alipayInfo);
        return ResponseEntity.ok().body(alipayInfo);
    }

    @GetMapping("/delete")
    @ApiOperation(value = "删除支付宝信息", notes = "删除支付宝信息")
    public ResponseEntity delete(String id){
        alipayInfoRepository.deleteById(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/deleteBatch")
    @ApiOperation(value = "批量删除支付宝信息", notes = "批量删除支付宝信息")
    public ResponseEntity delete(@RequestParam("id")String[] ids){
        ArrayList<AlipayInfo> alipayInfoList=new ArrayList<>();
        AlipayInfo alipayInfo=null;
        for(String id:ids){
            alipayInfo=new AlipayInfo();
            alipayInfo.setId(id);
            alipayInfoList.add(alipayInfo);
        }
        alipayInfoRepository.deleteAll(alipayInfoList);
        return ResponseEntity.ok().body(null);
    }

}

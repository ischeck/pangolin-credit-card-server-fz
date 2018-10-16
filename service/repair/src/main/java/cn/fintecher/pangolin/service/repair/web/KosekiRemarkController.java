package cn.fintecher.pangolin.service.repair.web;

import cn.fintecher.pangolin.common.utils.BeanUtils;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.repair.KosekiRemark;
import cn.fintecher.pangolin.service.repair.model.request.CreateKosekiRemarkRequest;
import cn.fintecher.pangolin.service.repair.model.request.KosekiRemarkSearchRequest;
import cn.fintecher.pangolin.service.repair.model.request.UpdateKosekiRemarkRequest;
import cn.fintecher.pangolin.service.repair.respository.KosekiRemarkRepository;
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
@RequestMapping("/api/kosekiRemark")
@Api(value = "户籍备注", description = "户籍备注")
public class KosekiRemarkController {
    private final Logger log = LoggerFactory.getLogger(KosekiRemarkController.class);

    @Autowired
    private KosekiRemarkRepository kosekiRemarkRepository;

    @GetMapping("/search")
    @ApiOperation(value = "查询户籍备注", notes = "查询户籍备注")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<KosekiRemark>> search(Pageable pageable, KosekiRemarkSearchRequest request) {

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(request.generateQueryBuilder()).build();
        log.debug("search koseki remark : {} query :{}", request.toString(), searchQuery.getQuery().toString());
        Page<KosekiRemark> searchPageResults = kosekiRemarkRepository.search(searchQuery);
        return new ResponseEntity<>(searchPageResults, HttpStatus.OK);
    }

    @GetMapping("/get")
    @ApiOperation(value = "获取户籍备注", notes = "获取户籍备注")
    public ResponseEntity get(String id) throws Exception {
        Optional<KosekiRemark> byId = kosekiRemarkRepository.findById(id);
        KosekiRemark kosekiRemark = byId.get();
        return ResponseEntity.ok().body(kosekiRemark);
    }
    
    @PostMapping("/insert")
    @ApiOperation(value = "创建户籍备注", notes = "创建户籍备注")
    public ResponseEntity insert(@RequestBody CreateKosekiRemarkRequest kosekiRemarkRequest) throws Exception {
        log.debug("Create koseki remark {}", kosekiRemarkRequest);
        KosekiRemark kosekiRemark = new KosekiRemark();
        BeanUtils.copyProperties(kosekiRemarkRequest, kosekiRemark);
        kosekiRemark.setImportDate(ZWDateUtil.getNowDateTime());
        kosekiRemarkRepository.save(kosekiRemark);
        return ResponseEntity.ok().body(kosekiRemark);
    }

    @PostMapping("/update")
    @ApiOperation(value = "修改户籍备注", notes = "修改户籍备注")
    public ResponseEntity update(@RequestBody UpdateKosekiRemarkRequest kosekiRemarkRequest) throws Exception {

        Optional<KosekiRemark> byId = kosekiRemarkRepository.findById(kosekiRemarkRequest.getId());
        KosekiRemark kosekiRemark = byId.get();
        BeanUtils.copyProperties(kosekiRemarkRequest, kosekiRemark);
        kosekiRemarkRepository.save(kosekiRemark);
        return ResponseEntity.ok().body(kosekiRemark);
    }

    @GetMapping("/delete")
    @ApiOperation(value = "删除户籍备注", notes = "删除户籍备注")
    public ResponseEntity delete(String id){
        kosekiRemarkRepository.deleteById(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/deleteBatch")
    @ApiOperation(value = "批量删除户籍备注", notes = "批量删除户籍备注")
    public ResponseEntity delete(@RequestParam("id")String[] ids){
        ArrayList<KosekiRemark> kosekiRemarkList=new ArrayList<>();
        KosekiRemark kosekiRemark=null;
        for(String id:ids){
            kosekiRemark=new KosekiRemark();
            kosekiRemark.setId(id);
            kosekiRemarkList.add(kosekiRemark);
        }
        kosekiRemarkRepository.deleteAll(kosekiRemarkList);
        return ResponseEntity.ok().body(null);
    }

}

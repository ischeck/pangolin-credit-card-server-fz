package cn.fintecher.pangolin.service.repair.web;

import cn.fintecher.pangolin.common.utils.BeanUtils;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.repair.KosekiData;
import cn.fintecher.pangolin.service.repair.model.request.CreateKosekiDataRequest;
import cn.fintecher.pangolin.service.repair.model.request.KosekiDataSearchRequest;
import cn.fintecher.pangolin.service.repair.model.request.UpdateKosekiDataRequest;
import cn.fintecher.pangolin.service.repair.respository.KosekiDataRepository;
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
@RequestMapping("/api/kosekiData")
@Api(value = "户籍资料", description = "户籍资料")
public class KosekiDataController {
    private final Logger log = LoggerFactory.getLogger(KosekiDataController.class);

    @Autowired
    private KosekiDataRepository kosekiDataRepository;

    @GetMapping("/search")
    @ApiOperation(value = "查询户籍资料", notes = "查询户籍资料")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<KosekiData>> search(Pageable pageable, KosekiDataSearchRequest request) {

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(request.generateQueryBuilder()).build();
        log.debug("search koseki data : {} query :{}", request.toString(), searchQuery.getQuery().toString());
        Page<KosekiData> searchPageResults = kosekiDataRepository.search(searchQuery);
        return new ResponseEntity<>(searchPageResults, HttpStatus.OK);
    }

    @GetMapping("/get")
    @ApiOperation(value = "获取户籍资料", notes = "获取户籍资料")
    public ResponseEntity get(String id) throws Exception {
        Optional<KosekiData> byId = kosekiDataRepository.findById(id);
        KosekiData kosekiData = byId.get();
        return ResponseEntity.ok().body(kosekiData);
    }

    @PostMapping("/insert")
    @ApiOperation(value = "创建户籍资料", notes = "创建户籍资料")
    public ResponseEntity insert(@RequestBody CreateKosekiDataRequest kosekiDataRequest) throws Exception {
        log.debug("Create koseki data {}", kosekiDataRequest);
        KosekiData kosekiData = new KosekiData();
        BeanUtils.copyProperties(kosekiDataRequest, kosekiData);
        kosekiData.setImportDate(ZWDateUtil.getNowDateTime());
        kosekiDataRepository.save(kosekiData);
        return ResponseEntity.ok().body(kosekiData);
    }

    @PostMapping("/update")
    @ApiOperation(value = "修改户籍资料", notes = "修改户籍资料")
    public ResponseEntity update(@RequestBody UpdateKosekiDataRequest kosekiDataRequest) throws Exception {

        Optional<KosekiData> byId = kosekiDataRepository.findById(kosekiDataRequest.getId());
        KosekiData kosekiData = byId.get();
        BeanUtils.copyProperties(kosekiDataRequest, kosekiData);
        kosekiDataRepository.save(kosekiData);
        return ResponseEntity.ok().body(kosekiData);
    }

    @GetMapping("/delete")
    @ApiOperation(value = "删除户籍资料", notes = "删除户籍资料")
    public ResponseEntity delete(String id){
        kosekiDataRepository.deleteById(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/deleteBatch")
    @ApiOperation(value = "批量删除户籍资料", notes = "批量删除户籍资料")
    public ResponseEntity delete(@RequestParam("id")String[] ids){
        ArrayList<KosekiData> kosekiDataList=new ArrayList<>();
        KosekiData kosekiData=null;
        for(String id:ids){
            kosekiData=new KosekiData();
            kosekiData.setId(id);
            kosekiDataList.add(kosekiData);
        }
        kosekiDataRepository.deleteAll(kosekiDataList);
        return ResponseEntity.ok().body(null);
    }

}

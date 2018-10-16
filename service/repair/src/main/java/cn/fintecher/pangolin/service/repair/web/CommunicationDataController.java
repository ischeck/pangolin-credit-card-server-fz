package cn.fintecher.pangolin.service.repair.web;

import cn.fintecher.pangolin.common.utils.BeanUtils;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.repair.CommunicationData;
import cn.fintecher.pangolin.service.repair.model.request.CommunicationDataSearchRequest;
import cn.fintecher.pangolin.service.repair.model.request.CreateCommunicationDataRequest;
import cn.fintecher.pangolin.service.repair.model.request.UpdateCommunicationDataRequest;
import cn.fintecher.pangolin.service.repair.respository.CommunicationDataRepository;
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
@RequestMapping("/api/communicationData")
@Api(value = "通讯资料", description = "通讯资料")
public class CommunicationDataController {
    private final Logger log = LoggerFactory.getLogger(CommunicationDataController.class);

    @Autowired
    private CommunicationDataRepository communicationDataRepository;

    @GetMapping("/search")
    @ApiOperation(value = "查询通讯资料", notes = "查询通讯资料")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<CommunicationData>> search(Pageable pageable, CommunicationDataSearchRequest request) {

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(request.generateQueryBuilder()).build();
        log.debug("search communication data : {} query :{}", request.toString(), searchQuery.getQuery().toString());
        Page<CommunicationData> searchPageResults = communicationDataRepository.search(searchQuery);
        return new ResponseEntity<>(searchPageResults, HttpStatus.OK);
    }

    @GetMapping("/get")
    @ApiOperation(value = "获取通讯资料", notes = "获取通讯资料")
    public ResponseEntity get(String id) throws Exception {
        Optional<CommunicationData> byId = communicationDataRepository.findById(id);
        CommunicationData communicationData = byId.get();
        return ResponseEntity.ok().body(communicationData);
    }

    @PostMapping("/insert")
    @ApiOperation(value = "创建通讯资料", notes = "创建通讯资料")
    public ResponseEntity insert(@RequestBody CreateCommunicationDataRequest communicationDataRequest) throws Exception {
        log.debug("Create communication data {}", communicationDataRequest);
        CommunicationData communicationData = new CommunicationData();
        BeanUtils.copyProperties(communicationDataRequest, communicationData);
        communicationData.setImportDate(ZWDateUtil.getNowDateTime());
        communicationDataRepository.save(communicationData);
        return ResponseEntity.ok().body(communicationData);
    }

    @PostMapping("/update")
    @ApiOperation(value = "修改通讯资料", notes = "修改通讯资料")
    public ResponseEntity update(@RequestBody UpdateCommunicationDataRequest communicationDataRequest) throws Exception {

        Optional<CommunicationData> byId = communicationDataRepository.findById(communicationDataRequest.getId());
        CommunicationData communicationData = byId.get();
        BeanUtils.copyProperties(communicationDataRequest, communicationData);
        communicationDataRepository.save(communicationData);
        return ResponseEntity.ok().body(communicationData);
    }

    @GetMapping("/delete")
    @ApiOperation(value = "删除通讯资料", notes = "删除通讯资料")
    public ResponseEntity delete(String id){
        communicationDataRepository.deleteById(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/deleteBatch")
    @ApiOperation(value = "批量删除通讯资料", notes = "批量删除通讯资料")
    public ResponseEntity delete(@RequestParam("id")String[] ids){
        ArrayList<CommunicationData> communicationDataList=new ArrayList<>();
        CommunicationData communicationData=null;
        for(String id:ids){
            communicationData=new CommunicationData();
            communicationData.setId(id);
            communicationDataList.add(communicationData);
        }
        communicationDataRepository.deleteAll(communicationDataList);
        return ResponseEntity.ok().body(null);
    }
    
}

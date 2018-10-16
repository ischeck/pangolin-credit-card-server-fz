package cn.fintecher.pangolin.service.repair.web;

import cn.fintecher.pangolin.common.utils.BeanUtils;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.repair.VillageCommitteeData;
import cn.fintecher.pangolin.service.repair.model.VillageCommitteeModel;
import cn.fintecher.pangolin.service.repair.model.request.CreateVillageCommitteeDataRequest;
import cn.fintecher.pangolin.service.repair.model.request.UpdateVillageCommitteeDataRequest;
import cn.fintecher.pangolin.service.repair.model.request.VillageCommitteeDataSearchRequest;
import cn.fintecher.pangolin.service.repair.respository.VillageCommitteeDataRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.modelmapper.ModelMapper;
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
import java.util.Objects;
import java.util.Optional;

/**
 * Created by hanwannan on 2017/8/27.
 */
@RestController
@RequestMapping("/api/villageCommitteeData")
@Api(value = "村委资料", description = "村委资料")
public class VillageCommitteeDataController {
    private final Logger log = LoggerFactory.getLogger(VillageCommitteeDataController.class);

    @Autowired
    private VillageCommitteeDataRepository villageCommitteeDataRepository;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/search")
    @ApiOperation(value = "查询村委资料", notes = "查询村委资料")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<VillageCommitteeModel>> search(Pageable pageable, VillageCommitteeDataSearchRequest request) {

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(request.generateQueryBuilder()).build();
        log.debug("search village committee data : {} query :{}", request.toString(), searchQuery.getQuery().toString());
        Page<VillageCommitteeModel> searchPageResults = villageCommitteeDataRepository.search(searchQuery).map(villageCommitteeData -> {
                    VillageCommitteeModel model = modelMapper.map(villageCommitteeData, VillageCommitteeModel.class);
                    return model;
                }
        );
        return new ResponseEntity<>(searchPageResults, HttpStatus.OK);
    }

    @GetMapping("/searchByKeyword")
    @ApiOperation(value = "关键字查询村委资料", notes = "关键字查询村委资料")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<VillageCommitteeData>> search(Pageable pageable, String keyword) {

    		if(!StringUtils.isNoneEmpty(keyword)) {
    			keyword="";
    		}
    	
        MultiMatchQueryBuilder qb = QueryBuilders.multiMatchQuery(keyword, "province","city","area","town","village");

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(qb).build();
        log.debug("search village committee data : {} query :{}", keyword, searchQuery.getQuery().toString());
        Page<VillageCommitteeData> searchPageResults = villageCommitteeDataRepository.search(searchQuery);
        return new ResponseEntity<>(searchPageResults, HttpStatus.OK);
    }

    @GetMapping("/get")
    @ApiOperation(value = "获取村委资料", notes = "获取村委资料")
    public ResponseEntity get(String id) throws Exception {
        Optional<VillageCommitteeData> byId = villageCommitteeDataRepository.findById(id);
        VillageCommitteeData villageCommitteeData = byId.get();
        return ResponseEntity.ok().body(villageCommitteeData);
    }

    @PostMapping("/insert")
    @ApiOperation(value = "创建村委资料", notes = "创建村委资料")
    public ResponseEntity insert(@RequestBody CreateVillageCommitteeDataRequest villageCommitteeDataRequest) throws Exception {
        log.debug("Create village committee data {}", villageCommitteeDataRequest);
        VillageCommitteeData villageCommitteeData = new VillageCommitteeData();
        BeanUtils.copyProperties(villageCommitteeDataRequest, villageCommitteeData);
        villageCommitteeData.setImportDate(ZWDateUtil.getNowDateTime());
        villageCommitteeDataRepository.save(villageCommitteeData);
        return ResponseEntity.ok().body(villageCommitteeData);
    }

    @PostMapping("/update")
    @ApiOperation(value = "修改村委资料", notes = "修改村委资料")
    public ResponseEntity update(@RequestBody UpdateVillageCommitteeDataRequest villageCommitteeDataRequest) throws Exception {

        Optional<VillageCommitteeData> byId = villageCommitteeDataRepository.findById(villageCommitteeDataRequest.getId());
        VillageCommitteeData villageCommitteeData = byId.get();
        BeanUtils.copyProperties(villageCommitteeDataRequest, villageCommitteeData);
        villageCommitteeDataRepository.save(villageCommitteeData);
        return ResponseEntity.ok().body(villageCommitteeData);
    }

    @GetMapping("/delete")
    @ApiOperation(value = "删除村委资料", notes = "删除村委资料")
    public ResponseEntity delete(String id){
        villageCommitteeDataRepository.deleteById(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/deleteBatch")
    @ApiOperation(value = "批量删除村委资料", notes = "批量删除村委资料")
    public ResponseEntity delete(@RequestParam("id")String[] ids){
        ArrayList<VillageCommitteeData> villageCommitteeDataList=new ArrayList<>();
        VillageCommitteeData villageCommitteeData=null;
        for(String id:ids){
            villageCommitteeData=new VillageCommitteeData();
            villageCommitteeData.setId(id);
            villageCommitteeDataList.add(villageCommitteeData);
        }
        villageCommitteeDataRepository.deleteAll(villageCommitteeDataList);
        return ResponseEntity.ok().body(null);
    }

}

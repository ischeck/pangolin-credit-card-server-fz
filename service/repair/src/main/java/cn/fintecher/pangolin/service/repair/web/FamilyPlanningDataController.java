package cn.fintecher.pangolin.service.repair.web;

import cn.fintecher.pangolin.common.utils.BeanUtils;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.repair.FamilyPlanningData;
import cn.fintecher.pangolin.service.repair.model.request.AddCredentialRequest;
import cn.fintecher.pangolin.service.repair.model.request.CreateFamilyPlanningDataRequest;
import cn.fintecher.pangolin.service.repair.model.request.FamilyPlanningDataSearchRequest;
import cn.fintecher.pangolin.service.repair.model.request.UpdateFamilyPlanningDataRequest;
import cn.fintecher.pangolin.service.repair.respository.FamilyPlanningDataRepository;
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
@RequestMapping("/api/familyPlanningData")
@Api(value = "计生资料", description = "计生资料")
public class FamilyPlanningDataController {
    private final Logger log = LoggerFactory.getLogger(FamilyPlanningDataController.class);

    @Autowired
    private FamilyPlanningDataRepository familyPlanningDataRepository;

    @GetMapping("/search")
    @ApiOperation(value = "查询计生资料", notes = "查询计生资料")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<FamilyPlanningData>> search(Pageable pageable, FamilyPlanningDataSearchRequest request) {

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(request.generateQueryBuilder()).build();
        log.debug("search family planning data : {} query :{}", request.toString(), searchQuery.getQuery().toString());
        Page<FamilyPlanningData> searchPageResults = familyPlanningDataRepository.search(searchQuery);
        return new ResponseEntity<>(searchPageResults, HttpStatus.OK);
    }

    @GetMapping("/get")
    @ApiOperation(value = "获取计生资料", notes = "获取计生资料")
    public ResponseEntity get(String id) throws Exception {
        Optional<FamilyPlanningData> byId = familyPlanningDataRepository.findById(id);
        FamilyPlanningData familyPlanningData = byId.get();
        return ResponseEntity.ok().body(familyPlanningData);
    }

    @PostMapping("/insert")
    @ApiOperation(value = "创建计生资料", notes = "创建计生资料")
    public ResponseEntity insert(@RequestBody CreateFamilyPlanningDataRequest familyPlanningDataRequest) throws Exception {
        log.debug("Create family planning data {}", familyPlanningDataRequest);
        FamilyPlanningData familyPlanningData = new FamilyPlanningData();
        BeanUtils.copyProperties(familyPlanningDataRequest, familyPlanningData);
        familyPlanningData.setImportDate(ZWDateUtil.getNowDateTime());
        familyPlanningDataRepository.save(familyPlanningData);
        return ResponseEntity.ok().body(familyPlanningData);
    }

    @PostMapping("/update")
    @ApiOperation(value = "修改计生资料", notes = "修改计生资料")
    public ResponseEntity update(@RequestBody UpdateFamilyPlanningDataRequest familyPlanningDataRequest) throws Exception {

        Optional<FamilyPlanningData> byId = familyPlanningDataRepository.findById(familyPlanningDataRequest.getId());
        FamilyPlanningData familyPlanningData = byId.get();
        BeanUtils.copyProperties(familyPlanningDataRequest, familyPlanningData);
        familyPlanningDataRepository.save(familyPlanningData);
        return ResponseEntity.ok().body(familyPlanningData);
    }

    @PostMapping("/addCredential")
    @ApiOperation(value = "添加证件", notes = "添加证件")
    public ResponseEntity addCredential(@RequestBody AddCredentialRequest addCredentialRequest) throws Exception {
        log.debug("Add credential {}", addCredentialRequest);
        Optional<FamilyPlanningData> byId = familyPlanningDataRepository.findById(addCredentialRequest.getId());
        FamilyPlanningData familyPlanningData = byId.get();
        familyPlanningData.setCredentialSet(addCredentialRequest.getCredentialSet());
        familyPlanningDataRepository.save(familyPlanningData);
        return ResponseEntity.ok().body(familyPlanningData);
    }

    @GetMapping("/delete")
    @ApiOperation(value = "删除计生资料", notes = "删除计生资料")
    public ResponseEntity delete(String id){
        familyPlanningDataRepository.deleteById(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/deleteBatch")
    @ApiOperation(value = "批量删除计生资料", notes = "批量删除计生资料")
    public ResponseEntity delete(@RequestParam("id")String[] ids){
        ArrayList<FamilyPlanningData> familyPlanningDataList=new ArrayList<>();
        FamilyPlanningData familyPlanningData=null;
        for(String id:ids){
            familyPlanningData=new FamilyPlanningData();
            familyPlanningData.setId(id);
            familyPlanningDataList.add(familyPlanningData);
        }
        familyPlanningDataRepository.deleteAll(familyPlanningDataList);
        return ResponseEntity.ok().body(null);
    }

}

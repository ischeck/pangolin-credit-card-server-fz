package cn.fintecher.pangolin.service.repair.web;

import cn.fintecher.pangolin.common.utils.BeanUtils;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.repair.SpecialTransferData;
import cn.fintecher.pangolin.entity.repair.SpecialTransferData;
import cn.fintecher.pangolin.service.repair.model.request.AddCredentialRequest;
import cn.fintecher.pangolin.service.repair.model.request.CreateSpecialTransferDataRequest;
import cn.fintecher.pangolin.service.repair.model.request.SpecialTransferDataSearchRequest;
import cn.fintecher.pangolin.service.repair.model.request.UpdateSpecialTransferDataRequest;
import cn.fintecher.pangolin.service.repair.respository.SpecialTransferDataRepository;
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
@RequestMapping("/api/specialTransferData")
@Api(value = "特调资料", description = "特调资料")
public class SpecialTransferDataController {
    private final Logger log = LoggerFactory.getLogger(SpecialTransferDataController.class);

    @Autowired
    private SpecialTransferDataRepository specialTransferDataRepository;

    @GetMapping("/search")
    @ApiOperation(value = "查询特调资料", notes = "查询特调资料")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<SpecialTransferData>> search(Pageable pageable, SpecialTransferDataSearchRequest request) {

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(request.generateQueryBuilder()).build();
        log.debug("search special transfer data : {} query :{}", request.toString(), searchQuery.getQuery().toString());
        Page<SpecialTransferData> searchPageResults = specialTransferDataRepository.search(searchQuery);
        return new ResponseEntity<>(searchPageResults, HttpStatus.OK);
    }

    @GetMapping("/get")
    @ApiOperation(value = "获取特调资料", notes = "获取特调资料")
    public ResponseEntity get(String id) throws Exception {
        Optional<SpecialTransferData> byId = specialTransferDataRepository.findById(id);
        SpecialTransferData specialTransferData = byId.get();
        return ResponseEntity.ok().body(specialTransferData);
    }

    @PostMapping("/insert")
    @ApiOperation(value = "创建特调资料", notes = "创建特调资料")
    public ResponseEntity insert(@RequestBody CreateSpecialTransferDataRequest specialTransferDataRequest) throws Exception {
        log.debug("Create special transfer data {}", specialTransferDataRequest);
        SpecialTransferData specialTransferData = new SpecialTransferData();
        BeanUtils.copyProperties(specialTransferDataRequest, specialTransferData);
        specialTransferData.setImportDate(ZWDateUtil.getNowDateTime());
        specialTransferDataRepository.save(specialTransferData);
        return ResponseEntity.ok().body(specialTransferData);
    }

    @PostMapping("/update")
    @ApiOperation(value = "修改特调资料", notes = "修改特调资料")
    public ResponseEntity update(@RequestBody UpdateSpecialTransferDataRequest specialTransferDataRequest) throws Exception {

        Optional<SpecialTransferData> byId = specialTransferDataRepository.findById(specialTransferDataRequest.getId());
        SpecialTransferData specialTransferData = byId.get();
        BeanUtils.copyProperties(specialTransferDataRequest, specialTransferData);
        specialTransferDataRepository.save(specialTransferData);
        return ResponseEntity.ok().body(specialTransferData);
    }

    @PostMapping("/addCredential")
    @ApiOperation(value = "添加证件", notes = "添加证件")
    public ResponseEntity addCredential(@RequestBody AddCredentialRequest addCredentialRequest) throws Exception {
        log.debug("Add credential {}", addCredentialRequest);
        Optional<SpecialTransferData> byId = specialTransferDataRepository.findById(addCredentialRequest.getId());
        SpecialTransferData specialTransferData = byId.get();
        specialTransferData.setCredentialSet(addCredentialRequest.getCredentialSet());
        specialTransferDataRepository.save(specialTransferData);
        return ResponseEntity.ok().body(specialTransferData);
    }

    @GetMapping("/delete")
    @ApiOperation(value = "删除特调资料", notes = "删除特调资料")
    public ResponseEntity delete(String id){
        specialTransferDataRepository.deleteById(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/deleteBatch")
    @ApiOperation(value = "批量删除特调资料", notes = "批量删除特调资料")
    public ResponseEntity delete(@RequestParam("id")String[] ids){
        ArrayList<SpecialTransferData> specialTransferDataList=new ArrayList<>();
        SpecialTransferData specialTransferData=null;
        for(String id:ids){
            specialTransferData=new SpecialTransferData();
            specialTransferData.setId(id);
            specialTransferDataList.add(specialTransferData);
        }
        specialTransferDataRepository.deleteAll(specialTransferDataList);
        return ResponseEntity.ok().body(null);
    }

}

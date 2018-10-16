package cn.fintecher.pangolin.service.repair.web;

import cn.fintecher.pangolin.common.utils.BeanUtils;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.repair.Relationship;
import cn.fintecher.pangolin.service.repair.model.request.BatchCreateRelationshipRequest;
import cn.fintecher.pangolin.service.repair.model.request.CreateRelationshipRequest;
import cn.fintecher.pangolin.service.repair.model.request.RelationshipSearchRequest;
import cn.fintecher.pangolin.service.repair.model.request.UpdateRelationshipRequest;
import cn.fintecher.pangolin.service.repair.respository.RelationshipRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Created by hanwannan on 2017/8/27.
 */
@RestController
@RequestMapping("/api/relationship")
@Api(value = "关联关系", description = "关联关系")
public class RelationshipController {
    private final Logger log = LoggerFactory.getLogger(RelationshipController.class);

    @Autowired
    private RelationshipRepository relationshipRepository;

    @GetMapping("/search")
    @ApiOperation(value = "查询关联关系", notes = "查询关联关系")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<Relationship>> search(Pageable pageable, RelationshipSearchRequest request) {

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(request.generateQueryBuilder()).build();
        log.debug("search relationship : {} query :{}", request.toString(), searchQuery.getQuery().toString());
        Page<Relationship> searchPageResults = relationshipRepository.search(searchQuery);
        return new ResponseEntity<>(searchPageResults, HttpStatus.OK);
    }

    @GetMapping("/get")
    @ApiOperation(value = "获取关联关系", notes = "获取关联关系")
    public ResponseEntity get(String id) throws Exception {
        Optional<Relationship> byId = relationshipRepository.findById(id);
        Relationship relationship = byId.get();
        return ResponseEntity.ok().body(relationship);
    }

    @PostMapping("/insert")
    @ApiOperation(value = "创建关联关系", notes = "创建关联关系")
    public ResponseEntity insert(@RequestBody CreateRelationshipRequest relationshipRequest) throws Exception {
        log.debug("Create relationship {}", relationshipRequest);
        Relationship relationship = new Relationship();
        BeanUtils.copyProperties(relationshipRequest, relationship);
        relationship.setImportDate(ZWDateUtil.getNowDateTime());
        relationshipRepository.save(relationship);
        return ResponseEntity.ok().body(relationship);
    }

    @PostMapping("/insertBatch")
    @ApiOperation(value = "批量创建关联关系", notes = "批量创建关联关系")
    public ResponseEntity insert(@RequestBody BatchCreateRelationshipRequest batchCreateRelationshipRequest) throws Exception {
        log.debug("Create relationship {}", batchCreateRelationshipRequest);
        ArrayList<Relationship> relationshipList=new ArrayList<>();
        for (CreateRelationshipRequest relationshipRequest:batchCreateRelationshipRequest.getCreateRelationshipRequestList()) {
            Relationship relationship = new Relationship();
            BeanUtils.copyProperties(relationshipRequest, relationship);
            relationshipRepository.save(relationship);
            relationshipList.add(relationship);
        }
        return ResponseEntity.ok().body(batchCreateRelationshipRequest);
    }

    @PostMapping("/update")
    @ApiOperation(value = "修改关联关系", notes = "修改关联关系")
    public ResponseEntity update(@RequestBody UpdateRelationshipRequest relationshipRequest) throws Exception {

        Optional<Relationship> byId = relationshipRepository.findById(relationshipRequest.getId());
        Relationship relationship = byId.get();
        BeanUtils.copyProperties(relationshipRequest, relationship);
        relationshipRepository.save(relationship);
        return ResponseEntity.ok().body(relationship);
    }

    @GetMapping("/delete")
    @ApiOperation(value = "删除关联关系", notes = "删除关联关系")
    public ResponseEntity delete(String id){
        relationshipRepository.deleteById(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/deleteBatch")
    @ApiOperation(value = "批量删除关联关系", notes = "批量删除关联关系")
    public ResponseEntity delete(@RequestParam("id")String[] ids){
        ArrayList<Relationship> relationshipList=new ArrayList<>();
        Relationship relationship=null;
        for(String id:ids){
            relationship=new Relationship();
            relationship.setId(id);
            relationshipList.add(relationship);
        }
        relationshipRepository.deleteAll(relationshipList);
        return ResponseEntity.ok().body(null);
    }

}

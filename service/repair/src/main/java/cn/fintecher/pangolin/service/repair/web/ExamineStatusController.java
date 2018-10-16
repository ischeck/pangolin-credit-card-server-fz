package cn.fintecher.pangolin.service.repair.web;

import cn.fintecher.pangolin.entity.repair.ExamineStatus;
import cn.fintecher.pangolin.service.repair.model.request.ExamineStatusSearchRequest;
import cn.fintecher.pangolin.service.repair.model.request.ReplyExamineStatusRequest;
import cn.fintecher.pangolin.service.repair.respository.ExamineStatusRepository;
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

import java.util.Optional;

/**
 * Created by hanwannan on 2017/8/27.
 */
@RestController
@RequestMapping("/api/examineStatus")
@Api(value = "检查情况", description = "检查情况")
public class ExamineStatusController {
    private final Logger log = LoggerFactory.getLogger(ExamineStatusController.class);

    @Autowired
    private ExamineStatusRepository examineStatusRepository;

    @GetMapping("/search")
    @ApiOperation(value = "查询检查情况", notes = "查询检查情况")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<ExamineStatus>> search(Pageable pageable, ExamineStatusSearchRequest request) {

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(request.generateQueryBuilder()).build();
        log.debug("search alipay info : {} query :{}", request.toString(), searchQuery.getQuery().toString());
        Page<ExamineStatus> searchPageResults = examineStatusRepository.search(searchQuery);
        return new ResponseEntity<>(searchPageResults, HttpStatus.OK);
    }

    @PostMapping("/reply")
    @ApiOperation(value = "申调回复", notes = "申调回复")
    public ResponseEntity addCredential(@RequestBody ReplyExamineStatusRequest replyExamineStatusRequest) throws Exception {
        log.debug("reply examine status {}", replyExamineStatusRequest);
        Optional<ExamineStatus> byId = examineStatusRepository.findById(replyExamineStatusRequest.getId());
        ExamineStatus examineStatus = byId.get();
        examineStatus.setReply(replyExamineStatusRequest.getReply());
        examineStatusRepository.save(examineStatus);
        return ResponseEntity.ok().body(examineStatus);
    }

}

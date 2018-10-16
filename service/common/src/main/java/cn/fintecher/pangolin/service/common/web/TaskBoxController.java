package cn.fintecher.pangolin.service.common.web;

import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.model.TaskBoxModel;
import cn.fintecher.pangolin.common.model.response.LoginResponse;
import cn.fintecher.pangolin.service.common.client.OperatorCommonClient;
import cn.fintecher.pangolin.service.common.model.QTaskBox;
import cn.fintecher.pangolin.service.common.model.TaskBox;
import cn.fintecher.pangolin.service.common.model.request.TaskBoxDeletedRequest;
import cn.fintecher.pangolin.service.common.respository.TaskBoxRepository;
import cn.fintecher.pangolin.service.common.service.WebSocketService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Objects;

/**
 * @Author:huyanmin
 * @Desc:
 * @Date:Create 2018/9/18
 */
@RestController
@RequestMapping("/api/taskBoxController")
@Api(value = "任务盒子", description = "任务盒子")
public class TaskBoxController {
    Logger logger = LoggerFactory.getLogger(TaskBoxController.class);

    @Autowired
    OperatorCommonClient operatorCommonClient;

    @Autowired
    WebSocketService webSocketService;

    @Autowired
    TaskBoxRepository taskBoxRepository;

    @PostMapping("/sendTaskByUserId")
    @ApiOperation(value = "发送任务盒子", notes = "发送任务盒子")
    public ResponseEntity sendMsgByUserId(@RequestBody TaskBoxModel taskBox,
                                          @RequestParam("userName") String userName) {
        webSocketService.sendTaskBoxMessage(userName, taskBox);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/queryTaskBox")
    @ApiOperation(value = "按条件分页查询", notes = "按条件分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
                    value = "页数 (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
                    value = "每页大小."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名(,asc|desc). ")
    })
    public ResponseEntity<Page<TaskBox>> query(@QuerydslPredicate(root = TaskBox.class) Predicate predicate,
                                               @RequestHeader(value = "X-UserToken") String token, @ApiIgnore Pageable pageable) {

        ResponseEntity<LoginResponse> userByToken = operatorCommonClient.getUserByToken(token);
        if (Objects.isNull(userByToken.getBody())) {
            throw new BadRequestException(null, "user", "this user is not login");
        }
        OperatorModel user = userByToken.getBody().getUser();
        BooleanBuilder builder = new BooleanBuilder(predicate);
        Pageable able = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), new Sort(Sort.Direction.DESC, "operatorTime"));
        builder.and(QTaskBox.taskBox.operator.eq(user.getId()));
        Page<TaskBox> page = taskBoxRepository.findAll(builder, able);
        return ResponseEntity.ok().body(page);
    }

    @DeleteMapping("/deletedTaskBox")
    @ApiOperation(value = "删除TaskBox", notes = "删除TaskBox")
    public ResponseEntity<TaskBox> deletedTaskBox(@RequestParam List<String> taskBoxIdList) {
        logger.info("删除TaskBox开始"+taskBoxIdList);
        Iterable<TaskBox> allById = taskBoxRepository.findAllById(taskBoxIdList);
        if(allById.iterator().hasNext()){
            taskBoxRepository.deleteAll(allById);
        }
        return ResponseEntity.ok(null);
    }

}

package cn.fintecher.pangolin.service.dataimp.web;

import cn.fintecher.pangolin.common.enums.ManagementType;
import cn.fintecher.pangolin.common.enums.MessageType;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.common.utils.ZWStringUtils;
import cn.fintecher.pangolin.entity.domain.Comment;
import cn.fintecher.pangolin.service.dataimp.repository.ImpCommentRepository;
import cn.fintecher.pangolin.service.dataimp.service.DataimpBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.IterableUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

@RestController
@RequestMapping("/api/schedule")
@Api(value = "定时调度", description = "定时调度")
public class ScheduleController {

    Logger log = LoggerFactory.getLogger(ScheduleController.class);

    @Autowired
    ImpCommentRepository impCommentRepository;
    @Autowired
    DataimpBaseService dataimpBaseService;

    @GetMapping("/sendCommentMsg")
    @ApiOperation(value = "发送备忘录消息", notes = "发送备忘录消息")
    public ResponseEntity sendCommentMsg(Integer minute) {
        log.info("发送备忘录消息" + ZWDateUtil.getDate());
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        qb.must(rangeQuery("reminderTime").gte(ZWDateUtil.getNowDate().getTime()));
        qb.must(rangeQuery("reminderTime").lt(new Date().getTime() + minute * 60000));
        qb.must(matchPhraseQuery("isRemind", ManagementType.NO.name()));
        List<Comment> commentList = IterableUtils.toList(impCommentRepository.search(qb));
        commentList.forEach(comment -> {
            dataimpBaseService.sendMessage(comment.getCommentContent(), comment.getOperatorUserName(), "备忘录提醒", MessageType.COMMENT, null);
            comment.setIsRemind(ManagementType.YES);
        });
        if(Objects.nonNull(commentList) && !commentList.isEmpty()) {
            impCommentRepository.saveAll(commentList);
        }
        return ResponseEntity.ok().body(null);
    }
}

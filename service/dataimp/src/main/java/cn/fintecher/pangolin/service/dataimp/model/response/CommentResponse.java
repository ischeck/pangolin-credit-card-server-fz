package cn.fintecher.pangolin.service.dataimp.model.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

/**
 * @Author:peishouwen
 * @Desc: 催员备忘录
 * @Date:Create in 16:11 2018/7/23
 */
@Data
@ApiModel(value = "CaseCollectorMemo", description = "催员备忘录")
public class CommentResponse {

    @ApiModelProperty(notes = "案件ID")
    private String caseId;

    @ApiModelProperty(notes = "提醒内容")
    private String commentContent;

    @ApiModelProperty(notes = "备注提醒时间")
    private Date reminderTime;
}

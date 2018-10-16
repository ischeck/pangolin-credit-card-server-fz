package cn.fintecher.pangolin.service.domain.model.request;

import cn.fintecher.pangolin.common.enums.CommentType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Author : huyanmin
 * @Description : 批注/评语/记事本新增request
 * @Date : 2018/8/6.
 */
@Data
@ApiModel(value = "CreateCommentRequest", description = "批注/评语/记事本新增request")
public class CreateCommentRequest {

    @ApiModelProperty(notes = "案件ID")
    private String caseId;

    @ApiModelProperty(notes = "申请案件的ID")
    private String applyId;

    @ApiModelProperty(notes = "类型")
    private CommentType commentType;

    @ApiModelProperty(notes = "内容")
    private String commentContent;

    @ApiModelProperty(notes = "操作人姓名")
    private String operatorName;

    @ApiModelProperty(notes = "备注提醒时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reminderTime;
}

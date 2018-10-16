package cn.fintecher.pangolin.entity.domain;

import cn.fintecher.pangolin.common.enums.CommentType;
import cn.fintecher.pangolin.common.enums.ManagementType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * Created by huyanmin 2018/07/16
 */

@Data
@Document(indexName = "comment", type = "comment", shards = 1, replicas = 0)
@ApiModel(value = "comment", description = "备注/评语/批注信息")
public class Comment {
    @Id
    private String id;

    @ApiModelProperty(notes = "案件ID")
    private String caseId;

    @ApiModelProperty(notes = "类型")
    private CommentType commentType;

    @ApiModelProperty(notes = "内容")
    private String commentContent;

    @ApiModelProperty(notes = "操作人名称")
    private String operatorName;

    @ApiModelProperty(notes = "操作人")
    private String operator;

    @ApiModelProperty(notes = "操作人用户名")
    private String operatorUserName;

    @ApiModelProperty(notes = "操作时间")
    @Field(type = FieldType.Date)
    private Date operatorTime;

    @ApiModelProperty(notes = "备注提醒时间")
    private Date reminderTime;

    @ApiModelProperty("是否已提醒")
    private ManagementType isRemind = ManagementType.NO;
}

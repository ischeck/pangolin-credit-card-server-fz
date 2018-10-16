package cn.fintecher.pangolin.entity.domain;

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
@Document(indexName = "CaseCollectorMemo", type = "CaseCollectorMemo", shards = 1, replicas = 0)
@ApiModel(value = "CaseCollectorMemo", description = "催员备忘录")
public class CaseCollectorMemo {
    @Id
    private String id;

    @ApiModelProperty(notes = "案件ID")
    private String caseId;

    @ApiModelProperty(notes = "提醒时间")
    private Date remindDateTime;

    @ApiModelProperty(notes = "提醒内容")
    private String remindMemo;

    @ApiModelProperty(notes = "操作员")
    private String operator;

    @ApiModelProperty(notes = "操作时间")
    private Date operatorTime;
}

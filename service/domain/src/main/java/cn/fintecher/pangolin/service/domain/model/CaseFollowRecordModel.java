package cn.fintecher.pangolin.service.domain.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Created by BBG on 2018/8/2.
 */
@Data
public class CaseFollowRecordModel {

    @ApiModelProperty(notes = "联络对象")
    private String target;

    @ApiModelProperty(notes = "联络对象姓名")
    private String targetName;

    @ApiModelProperty(notes = "跟进内容")
    private String content;

    @ApiModelProperty(notes = "联系电话")
    private String contactPhone;

    @ApiModelProperty(notes = "操作人姓名")
    private String operatorName;

    @ApiModelProperty(notes = "操作时间")
    private Date operatorTime;

    @ApiModelProperty(notes = "联络结果")
    private String contactResult;

}

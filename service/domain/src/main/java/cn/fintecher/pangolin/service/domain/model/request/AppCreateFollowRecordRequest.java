package cn.fintecher.pangolin.service.domain.model.request;

import cn.fintecher.pangolin.common.enums.FollowType;
import cn.fintecher.pangolin.entity.domain.FileInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AppCreateFollowRecordRequest {

    @ApiModelProperty(notes = "案件信息ID")
    private String caseId;

    @ApiModelProperty(notes = "联络对象/联系状态/联络人")
    private String target;

    @ApiModelProperty(notes = "联络对象姓名")
    private String targetName;

    @ApiModelProperty(notes = "行动代码/联络结果/电催摘要/催收代码")
    private String contactResult;

    @ApiModelProperty(notes = "外访摘要")
    private String collectionOutResult;

    @ApiModelProperty(notes = "地址状态")
    private String addrStatus;

    @ApiModelProperty(notes = "地址类型")
    private String addrType;

    @ApiModelProperty(notes = "详细地址")
    private String detail;

    @ApiModelProperty(notes = "催收记录")
    private String content;

    @ApiModelProperty(notes = "承诺还款日期")
    private Date promiseDate;

    @ApiModelProperty(notes = "提醒时间")
    private Date follNextDate;

    @ApiModelProperty(notes = "跟进方式")
    private FollowType type;

    @ApiModelProperty(notes = "结果CODE")
    private String resultCode;

    @ApiModelProperty(notes = "凭证")
    private List<FileInfo> certificate;

    @ApiModelProperty(notes = "视频")
    private List<FileInfo> video;

    @ApiModelProperty(notes = "录音")
    private List<FileInfo> record;

    @ApiModelProperty("外访员")
    private String visitors;

    @ApiModelProperty(notes = "催记方式 0-自动 1-手动")
    private Integer collectionWay;

}

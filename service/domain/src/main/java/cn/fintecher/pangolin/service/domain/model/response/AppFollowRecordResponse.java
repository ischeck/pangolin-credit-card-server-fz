package cn.fintecher.pangolin.service.domain.model.response;

import cn.fintecher.pangolin.entity.domain.FileInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AppFollowRecordResponse {

    @ApiModelProperty(notes = "联络对象/联系状态/联络人")
    private String target;

    @ApiModelProperty(notes = "联络对象姓名")
    private String targetName;

    @ApiModelProperty(notes = "行动代码/联络结果/电催摘要/催收代码")
    private String contactResult;

    @ApiModelProperty(notes = "操作人姓名")
    private String operatorName;

    @ApiModelProperty(notes = "操作人部门")
    private String operatorDeptName;

    @ApiModelProperty(notes = "操作时间")
    private Date operatorTime;

    @ApiModelProperty(notes = "详细地址")
    private String detail;

    @ApiModelProperty(notes = "地址状态")
    private String addrStatus;

    @ApiModelProperty(notes = "地址类型")
    private String addrType;

    @ApiModelProperty(notes = "承诺还款日期")
    private Date promiseDate;

    @ApiModelProperty(notes = "催收方式/催收措施")
    private String collectionType;

    @ApiModelProperty(notes = "提醒时间")
    private Date follNextDate;

    @ApiModelProperty(notes = "催收记录")
    private String content;

    @ApiModelProperty(notes = "凭证")
    private List<FileInfo> certificate;

    @ApiModelProperty(notes = "视频")
    private List<FileInfo> video;

    @ApiModelProperty(notes = "录音")
    private List<FileInfo> record;

}

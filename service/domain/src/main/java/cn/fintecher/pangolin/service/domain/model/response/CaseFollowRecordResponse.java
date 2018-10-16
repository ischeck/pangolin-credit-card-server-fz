package cn.fintecher.pangolin.service.domain.model.response;

import cn.fintecher.pangolin.common.enums.FollowType;
import cn.fintecher.pangolin.entity.domain.FileInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by BBG on 2018/8/2.
 */
@Data
public class CaseFollowRecordResponse {

    @ApiModelProperty(notes = "跟进记录的id")
    private String id;

    @ApiModelProperty(notes = "联络对象")
    private String target;

    @ApiModelProperty(notes = "联络对象姓名")
    private String targetName;

    @ApiModelProperty(notes = "跟进方式")
    private FollowType type;

    @ApiModelProperty(notes = "跟进内容展示")
    private String contentView;

    @ApiModelProperty(notes = "电话联系状态")
    private String contactState;

    @ApiModelProperty(notes = "电话类型")
    private String phoneType;

    @ApiModelProperty(notes = "联系电话")
    private String contactPhone;

    @ApiModelProperty(notes = "联络结果")
    private String contactResult;

    @ApiModelProperty(notes = "操作人姓名")
    private String operatorName;

    @ApiModelProperty(notes = "操作时间")
    private Date operatorTime;

    @ApiModelProperty(notes = "承诺还款金额")
    private Double promiseAmt;

    @ApiModelProperty(notes = "地址状态")
    private String addrStatus;

    @ApiModelProperty(notes = "地址类型")
    private String addrType;

    @ApiModelProperty(notes = "录音地址")
    private String opUrl;

    @ApiModelProperty(notes = "备注")
    private String remark;

    @ApiModelProperty(notes = "录音文件目录")
    private String filePath;

    @ApiModelProperty(notes = "跟进记录文件上传目录")
    private List<String> fileIds;

    @ApiModelProperty(notes = "凭证")
    private List<FileInfo> certificate;

    @ApiModelProperty(notes = "视频")
    private List<FileInfo> video;

    @ApiModelProperty(notes = "录音")
    private List<FileInfo> record;
}

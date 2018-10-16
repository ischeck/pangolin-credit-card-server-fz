package cn.fintecher.pangolin.service.domain.model.request;

import cn.fintecher.pangolin.common.enums.FollowType;
import cn.fintecher.pangolin.entity.domain.FileInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;

/**
 * Created by BBG on 2018/8/2.
 */
@Data
public class CreateFollowRecordModel {

    @ApiModelProperty(notes = "案件信息ID")
    private String personalContactId;

    @ApiModelProperty(notes = "案件信息ID")
    private String caseId;

    @ApiModelProperty(notes = "联络对象/联系状态/联络人")
    private String target;

    @ApiModelProperty(notes = "联络对象姓名")
    private String targetName;

    @ApiModelProperty(notes = "催收日期")
    @Field(type = FieldType.Date)
    private Date followTime;

    @ApiModelProperty(notes = "行动代码/联络结果/电催摘要/催收代码")
    private String contactResult;

    @ApiModelProperty(notes = "外访摘要")
    private String collectionOutResult;

    @ApiModelProperty(notes = "催收状态")
    private String collectionStatus;

    @ApiModelProperty(notes = "催收方式/催收措施")
    private String collectionType;

    @ApiModelProperty(notes = "电话类型/联络类型/联系类型")
    private String phoneType;

    @ApiModelProperty(notes = "电话号码/(号码/地址)/(电话/地址)")
    private String contactPhone;

    @ApiModelProperty(notes = "号码状态")
    private String contactState;

    @ApiModelProperty(notes = "地址状态")
    private String addrStatus;

    @ApiModelProperty(notes = "地址类型")
    private String addrType;

    @ApiModelProperty(notes = "详细地址")
    private String detail;

    @ApiModelProperty(notes = "催收记录")
    private String content;

    @ApiModelProperty(notes = "快捷录入")
    private String quickRecord;

    @ApiModelProperty(notes = "承诺还款标识 0-没有承诺 1-有承诺")
    private Integer promiseFlag;

    @ApiModelProperty(notes = "承诺还款金额")
    private Double promiseAmt;

    @ApiModelProperty(notes = "承诺还款日期")
    @Field(type = FieldType.Date)
    private Date promiseDate;

    @ApiModelProperty(notes = "已还款金额")
    private Double hasPaymentAmt;

    @ApiModelProperty(notes = "已还款日期")
    @Field(type = FieldType.Date)
    private Date hasPaymentDate;

    @ApiModelProperty(notes = "是否提醒")
    private String follNextFlag;

    @ApiModelProperty(notes = "提醒时间")
    @Field(type = FieldType.Date)
    private Date follNextDate;

    @ApiModelProperty(notes = "标红处理")
    private String redRemark;

    @ApiModelProperty(notes = "跟进备注")
    private String remark;

    @ApiModelProperty(notes = "要点标记")
    private String importRemark;

    @ApiModelProperty(notes = "信息更新")
    private String informationUpdate;

    @ApiModelProperty(notes = "跟进方式")
    private FollowType type;

    @ApiModelProperty(notes = "跟进记录")
    private String contantView;

    @ApiModelProperty(notes = "结果CODE")
    private String resultCode;

    @ApiModelProperty(notes = "下次跟进提醒内容")
    private String follNextContent;

    @ApiModelProperty(notes = "通话ID")
    private String taskId;

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

    @ApiModelProperty("协催ID")
    private String assistId;

    @ApiModelProperty(notes = "跟进记录文件上传目录")
    private List<String> fileIds;

}

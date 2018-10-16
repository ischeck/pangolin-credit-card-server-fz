package cn.fintecher.pangolin.entity.domain;

import cn.fintecher.pangolin.common.enums.FollowType;
import cn.fintecher.pangolin.common.enums.ManagementType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;

/**
 * @author : huyanmin
 * @Description : 跟进记录实体
 * @Date : 15:49 2018/7/5
 */
@Data
@Document(indexName = "case_followup_record", type = "case_followup_record", shards = 1, replicas = 0)
@ApiModel(value = "caseFollowupRecord", description = "卡信息")
public class CaseFollowupRecord {

    @Id
    @ApiModelProperty(notes = "跟进id")
    private String id;

    @ApiModelProperty(notes = "客户信息ID")
    private String personalId;

    @ApiModelProperty(notes = "案件信息ID")
    private String caseId;

    @ApiModelProperty(notes = "联络对象/联系状态/联络人")
    private String target;

    @ApiModelProperty(notes = "联络对象姓名")
    private String targetName;

    @ApiModelProperty(notes = "催收日期")
    @Field(type = FieldType.Date)
    private Date followTime;

    @ApiModelProperty(notes = "外访员")
    private String visitors;

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
    private String contentView;

    @ApiModelProperty(notes = "结果CODE")
    private String resultCode;

    @ApiModelProperty(notes = "操作人")
    private String operator;

    @ApiModelProperty(notes = "操作人姓名")
    private String operatorName;

    @ApiModelProperty(notes = "操作人部门")
    private String operatorDeptName;

    @ApiModelProperty(notes = "操作时间")
    @Field(type = FieldType.Date)
    private Date operatorTime;

    @ApiModelProperty(notes = "是否还款")
    private ManagementType isPaid;

    @ApiModelProperty(notes = "是否协商减免")
    private ManagementType isRemit;

    @ApiModelProperty(notes = "减免金额")
    private Double remitAmt;

    @ApiModelProperty(notes = "下次跟进提醒内容")
    private String follNextContent;

    @ApiModelProperty(notes = "通话ID")
    private String taskId;

    @ApiModelProperty(notes = "录音地址")
    private String opUrl;

    @ApiModelProperty(notes = "录音下载标识")
    private Integer loadFlag;

    @ApiModelProperty(notes = "催记方式 1-自动 0-手动")
    private Integer collectionWay;

    @ApiModelProperty(notes = "呼叫开始时间")
    @Field(type = FieldType.Date)
    private Date startTime;

    @ApiModelProperty(notes = "呼叫结束时间")
    @Field(type = FieldType.Date)
    private Date endTime;

    @ApiModelProperty(notes = "通话时长默认为秒")
    private Integer connSecs;

    @ApiModelProperty(notes = "录音文件名称")
    private String fileName;

    @ApiModelProperty(notes = "录音文件目录")
    private String filePath;

    @ApiModelProperty(notes = "跟进记录文件上传目录")
    private List<String> fileIds;

    @ApiModelProperty(notes = "数据来源")
    private Integer source;

    @ApiModelProperty(notes = "凭证")
    private List<FileInfo> certificate;

    @ApiModelProperty(notes = "视频")
    private List<FileInfo> video;

    @ApiModelProperty(notes = "录音")
    private List<FileInfo> record;

    @ApiModelProperty("协催ID")
    private String assistId;
}
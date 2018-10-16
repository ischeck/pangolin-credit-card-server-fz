package cn.fintecher.pangolin.entity.repair;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

/**
 * @Author : hanwannan
 * @Description : 数据修复-检查情况
 * @Date : 2018/8/28.
 */
@Data
@Document(indexName = "examine_status", type = "examine_status", shards = 1, replicas = 0)
@ApiModel(value = "ExamineStatus", description = "检查情况")
public class ExamineStatus {

    @Id
    @ApiModelProperty(notes = "特定字段做Md5生成")
    private String id;

    @ApiModelProperty(notes = "姓名")
    private String name;

    @ApiModelProperty("证件号")
    private String idNo;

    @ApiModelProperty("申请人")
    private String applyPerson;

    @ApiModelProperty("银行")
    private String bank;

    @ApiModelProperty("是否回复")
    private String replyStatus;

    @ApiModelProperty("申请情况")
    private String applyStatus;

    @ApiModelProperty("案件地区")
    private String caseArea;

    @ApiModelProperty("申调地区")
    private String applyTransferArea;

    @ApiModelProperty("申调日期")
    private Date applyTransferDate;

    @ApiModelProperty("回复日期")
    private Date replyDate;

    @ApiModelProperty("最后一次调取结果")
    private String latelyTransferResult;

    @ApiModelProperty("申调项目")
    private String applyTransferProject;

    @ApiModelProperty("被申请人姓名")
    private String respondentName;

    @ApiModelProperty("被申请人身份证")
    private Double respondentIdNo;

    @ApiModelProperty("申调说明")
    private String applyTransferExplain;

    @ApiModelProperty(notes = "申调回复")
    private Reply reply;

}

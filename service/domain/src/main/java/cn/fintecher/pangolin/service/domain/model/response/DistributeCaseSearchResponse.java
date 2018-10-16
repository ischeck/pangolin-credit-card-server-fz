package cn.fintecher.pangolin.service.domain.model.response;

import cn.fintecher.pangolin.entity.domain.Personal;
import cn.fintecher.pangolin.entity.managentment.Operator;
import cn.fintecher.pangolin.entity.managentment.Principal;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author : BBG
 * @Description : 案件查询结果
 * @Date : 2018/7/30.
 */
@Data
public class DistributeCaseSearchResponse {

    @ApiModelProperty("案件Id")
    private String id;

    @ApiModelProperty("批次号")
    private String batchNumber;

    @ApiModelProperty(notes = "委托方")
    private String principalName;

    @ApiModelProperty(notes = "委案日期")
    private Date delegationDate;

    @ApiModelProperty(notes = "结案日期")
    private Date endCaseDate;

    @ApiModelProperty(notes = "委案城市")
    private String city;

    @ApiModelProperty(notes = "案件编号")
    private String caseNumber;

    @ApiModelProperty(notes = "姓名")
    private String personalName;

    @ApiModelProperty(notes = "证件号")
    private String certificateNo;

    @ApiModelProperty(notes = "帐号")
    private String account;

    @ApiModelProperty(notes = "委案金额(人民币)")
    private Double overdueAmtTotal=0.0;

    @ApiModelProperty(notes = "委案总金额(美元)")
    private Double overdueAmtTotalDollar=0.0;

    @ApiModelProperty(notes = "本金(人民币)")
    private Double capitalAmt=0.0;

    @ApiModelProperty(notes = "本金(美元)")
    private Double capitalAmtDollar=0.0;

    @ApiModelProperty(notes = "欠款(人民币)")
    private Double leftAmt=0.0;

    @ApiModelProperty(notes = "欠款(美元)")
    private Double leftAmtDollar=0.0;

    @ApiModelProperty(notes = "逾期期数")
    private Integer overduePeriods=0;

    @ApiModelProperty(notes = "逾期天数")
    private Integer overdueDays=0;

    @ApiModelProperty(notes = "手别")
    private String handsNumber;

    @ApiModelProperty(notes = "当前部门名称")
    private String detaptName;

    @ApiModelProperty(notes = "催收员")
    private String currentCollector;

    @ApiModelProperty(notes = "上一个催收员")
    private String latelyCollector;

}

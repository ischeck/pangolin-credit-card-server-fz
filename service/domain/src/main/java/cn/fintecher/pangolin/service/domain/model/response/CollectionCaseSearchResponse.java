package cn.fintecher.pangolin.service.domain.model.response;

import cn.fintecher.pangolin.entity.domain.Personal;
import cn.fintecher.pangolin.entity.managentment.Operator;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.Set;

/**
 * @Author : huyanmin
 * @Description : 案件查询结果
 * @Date : 2018/7/30.
 */
@Data
public class CollectionCaseSearchResponse {

    @ApiModelProperty("案件Id")
    private String id;

    @ApiModelProperty("批次号")
    private String batchNumber;

    @ApiModelProperty("案件编号")
    private String caseNumber;

    @ApiModelProperty("账号")
    private String account;

    @ApiModelProperty("姓名")
    private Personal personal;

    @ApiModelProperty(notes = "案件状态")
    private Set<String> caseStatus;

    @ApiModelProperty(notes = "催收状态")
    private Set<String> collectionStatus;

    @ApiModelProperty(notes = "手数")
    private String handsNumber;

    @ApiModelProperty(notes = "逾期期数")
    private Integer overduePeriods;

    @ApiModelProperty(notes = "跟进时间")
    private Date followTime;

    @ApiModelProperty(notes = "地区")
    private String city;

    @ApiModelProperty(notes = "催收员")
    private Operator currentCollector;

    @ApiModelProperty(notes = "催计数")
    private Integer collectionRecordCount;

    @ApiModelProperty(notes = "催计数总数")
    private Integer collectionTotalRecordCount;

    @ApiModelProperty(notes = "颜色")
    private String color;

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
}

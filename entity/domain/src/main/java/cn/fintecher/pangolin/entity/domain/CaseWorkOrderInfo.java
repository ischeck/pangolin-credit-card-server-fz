package cn.fintecher.pangolin.entity.domain;

import cn.fintecher.pangolin.common.annotation.ExcelAnno;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @Author:peishouwen
 * @Desc: 工单信息
 * @Date:Create in 16:06 2018/7/23
 */
@Data
@Document(indexName = "case_work_order_info", type = "case_work_order_info", shards = 1, replicas = 0)
@ApiModel(value = "CaseWorkOrderInfo", description = "工单信息")
public class CaseWorkOrderInfo {
    @Id
    private String id;

    @ApiModelProperty(notes = "案件ID")
    private String caseId;

    @ApiModelProperty(notes = "批次号")
    private String batchNumber;

    @ApiModelProperty(notes = "案件编号")
    private String caseNumber;

    @ApiModelProperty(notes = "帐号")
    private String account;

    @ApiModelProperty(notes = "卡号")
    private String cardNo;

    @ApiModelProperty(notes = "证件号")
    private String certificateNo;

    @ApiModelProperty(notes = "工单日期")
    @Field(type = FieldType.Date)
    private Date orderDate;

    @ApiModelProperty(notes = "工单信息")
    private String orderInfo;

    @ApiModelProperty(notes = "操作员")
    private String operator;

    @ApiModelProperty(notes = "操作时间")
    @Field(type = FieldType.Date)
    private Date operatorTime;
}

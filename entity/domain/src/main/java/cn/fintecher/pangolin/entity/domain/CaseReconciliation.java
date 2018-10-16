package cn.fintecher.pangolin.entity.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

/**
 * @Author:peishouwen
 * @Desc: 对账导入
 * @Date:Create in 15:38 2018/7/23
 */
@Data
@Document(indexName = "case_reconciliation", type = "case_reconciliation", shards = 1, replicas = 0)
@ApiModel(value = "CaseReconciliation", description = "对账导入")
public class CaseReconciliation {
    @Id
    private String id;

    @ApiModelProperty(notes = "批次号")
    private String batchNumber;

    @ApiModelProperty(notes = "对账账号，唯一标识用于和案件关联")
    private String account;

    @ApiModelProperty(notes = "更新日期")
    private Date date;

    @ApiModelProperty(notes = "币种")
    private String currency;

    @ApiModelProperty(notes = "余额")
    private Double leftAmt;

    @ApiModelProperty(notes = "还款总金额")
    private Double payAmtTotal;

    @ApiModelProperty(notes = "备注")
    private String remark;

    @ApiModelProperty(notes = "操作员")
    private String operator;

    @ApiModelProperty(notes = "操作时间")
    private Date operatorTime;
}

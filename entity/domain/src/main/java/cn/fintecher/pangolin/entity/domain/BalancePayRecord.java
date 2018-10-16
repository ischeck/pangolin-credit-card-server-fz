package cn.fintecher.pangolin.entity.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @Author:gaobeibei
 * @Desc: 对账还款记录
 * @Date:Create in 15:36 2018/9/12
 */
@Data
@Document(indexName = "balance_pay_record", type = "balance_pay_record", shards = 1, replicas = 0)
@ApiModel(value = "BalancePayRecord", description = "对账还款记录")
public class BalancePayRecord {
    @Id
    @ApiModelProperty(notes = "ID")
    private String id;

    @ApiModelProperty(notes = "案件ID")
    private String caseId;

    @ApiModelProperty(notes = "还款日期")
    @Field(type = FieldType.Date)
    private Date payDate;

    @ApiModelProperty(notes = "还款金额")
    private Double payAmt;

    @ApiModelProperty(notes = "工号")
    private String employeeNumber;

    @ApiModelProperty(notes = "催收员ID")
    private String collectorId;

    @ApiModelProperty(notes = "催收员名称")
    private String collectorName;

    @ApiModelProperty(notes = "部门")
    private String organizationName;
}

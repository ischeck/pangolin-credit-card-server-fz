package cn.fintecher.pangolin.common.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * 对账查询model
 * Created by huyanmin on 2018/8/6.
 */
@Data
public class CaseBillRecordModel {

    @ApiModelProperty(notes = "Base案件id")
    private String caseId;

    @ApiModelProperty(notes = "余额")
    private Double leftAmt;

    @ApiModelProperty(notes = "余额美元")
    private Double leftAmtDollar;

    @ApiModelProperty(notes = "还款总金额")
    private Double payAmtTotal;

    @ApiModelProperty(notes = "对账日期")
    private Date updateDate;

}

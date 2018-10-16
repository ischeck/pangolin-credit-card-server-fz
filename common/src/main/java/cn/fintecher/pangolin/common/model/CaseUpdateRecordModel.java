package cn.fintecher.pangolin.common.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 案件更新model
 * Created by huyanmin on 2018/8/6.
 */
@Data
public class CaseUpdateRecordModel {

    @ApiModelProperty(notes = "Base案件id")
    private String baseCaseId;

    @ApiModelProperty(notes = "余额")
    private Double leftAmt;

    @ApiModelProperty(notes = "余额美元")
    private Double leftAmtDollar;

    @ApiModelProperty(notes = "对账日期")
    private Date updateDate;

    @ApiModelProperty(notes = "备注")
    private String remark;

}

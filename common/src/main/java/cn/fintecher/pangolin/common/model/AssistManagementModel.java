package cn.fintecher.pangolin.common.model;

import cn.fintecher.pangolin.common.annotation.ExcelAnno;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author:huyanmin
 * @Desc:导出关联主键字段
 * @Date:Create in 2018/8/31
 */
@Data
public class AssistManagementModel {

    @ApiModelProperty(notes = "客户姓名")
    private String personalName;

    @ApiModelProperty(notes = "证件号码")
    private String certificateNo;

    @ApiModelProperty("委案金额")
    private Double overdueAmtTotal;

    @ApiModelProperty("欠款")
    private Double leftAmt;

    @ApiModelProperty(notes = "卡号")
    private String cardNo;

    @ApiModelProperty(notes = "账户号")
    private String account;

    @ApiModelProperty(notes = "案件编号")
    private String caseNumber;

}

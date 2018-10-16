package cn.fintecher.pangolin.service.domain.model.response;

import cn.fintecher.pangolin.common.enums.CaseDataStatus;
import cn.fintecher.pangolin.common.enums.CaseLeaveFlag;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Created by BBG on 2018/8/10.
 */
@Data
public class CaseStateQueryResponse {
    @ApiModelProperty("案件ID")
    private String id;

    @ApiModelProperty(notes = "案件号")
    private String caseNumber;

    @ApiModelProperty("账号")
    private String account;

    @ApiModelProperty(notes = "批次号")
    private String batchNumber;

    @ApiModelProperty(notes = "委托方")
    private String principalName;

    @ApiModelProperty("身份证号码")
    private String certificateNo;

    @ApiModelProperty("手机号")
    private String selfPhoneNo;

    @ApiModelProperty("客户姓名")
    private String personalName;

    @ApiModelProperty("催收员")
    private String currentCollectorName;

    @ApiModelProperty(notes = "城市")
    private String city;

    @ApiModelProperty(notes = "手数")
    private String handsNumber;

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

    @ApiModelProperty(notes = "数据状态")
    private CaseDataStatus caseDataStatus;

    @ApiModelProperty(notes = "留案标识")
    private CaseLeaveFlag leaveFlag;

    @ApiModelProperty(notes = "委案日期")
    private Date delegationDate;

    @ApiModelProperty(notes = "结案日期")
    private Date endCaseDate;
}

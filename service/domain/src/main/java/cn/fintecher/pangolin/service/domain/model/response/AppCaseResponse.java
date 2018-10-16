package cn.fintecher.pangolin.service.domain.model.response;

import cn.fintecher.pangolin.common.enums.AssistFlag;
import cn.fintecher.pangolin.common.enums.CaseLeaveFlag;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AppCaseResponse {

    @ApiModelProperty(notes = "ID")
    private String id;

    @ApiModelProperty(notes = "案件Id")
    private String caseId;

    @ApiModelProperty("姓名")
    private String personalName;

    @ApiModelProperty(notes = "性别")
    private String sex;

    @ApiModelProperty("年龄")
    private Integer age;

    @ApiModelProperty(notes = "协助标识")
    private AssistFlag assistFlag;

    @ApiModelProperty("协催地址")
    private String addressDetail;

    @ApiModelProperty(notes = "手数")
    private String handsNumber;

    @ApiModelProperty("案件金额")
    private Double leftAmt;

    @ApiModelProperty(notes = "联络结果")
    private String contactResult;

    @ApiModelProperty("电话")
    private String phone;

    @ApiModelProperty("留案标识")
    private CaseLeaveFlag leaveFlag;

    @ApiModelProperty("委托方ID")
    private String principalId;
}

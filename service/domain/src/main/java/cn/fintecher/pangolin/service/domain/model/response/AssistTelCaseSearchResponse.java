package cn.fintecher.pangolin.service.domain.model.response;

import cn.fintecher.pangolin.common.enums.AssistFlag;
import cn.fintecher.pangolin.common.enums.AssistStatus;
import cn.fintecher.pangolin.entity.domain.Personal;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

/**
 * @Author : huyanmin
 * @Description : 协催电话案件查询结果
 * @Date : 2018/7/30.
 */
@Data
public class AssistTelCaseSearchResponse {

    @ApiModelProperty("案件Id")
    private String id;

    @ApiModelProperty("案件Id")
    private String caseId;

    @ApiModelProperty("案件Id")
    private String assistId;

    @ApiModelProperty("批次号")
    private String batchNumber;

    @ApiModelProperty("姓名")
    private String personalName;

    @ApiModelProperty("客户Id")
    private String personalId;

    @ApiModelProperty("客户身份证号码")
    private String certificateNo;

    @ApiModelProperty(notes = "协助状态")
    private AssistStatus assistStatus;

    @ApiModelProperty(notes = "委托方Id")
    private String principalId;

    @ApiModelProperty(notes = "委托方姓名")
    private String principalName;

    @ApiModelProperty(notes = "系统状态")
    private Set<String> caseStatus;

    @ApiModelProperty(notes = "手工状态")
    private Set<String> collectionStatus;

    @ApiModelProperty(notes = "案件编号")
    private String caseNumber;

    @ApiModelProperty(notes = "帐号")
    private String account;

    @ApiModelProperty(notes = "余额")
    private Double leftAmt;

    @ApiModelProperty(notes = "美元余额")
    private Double leftAmtDollar;

    @ApiModelProperty(notes = "还款状态")
    private String payStatus;

    @ApiModelProperty(notes = "地区")
    private String city;

    @ApiModelProperty(notes = "手数")
    private String handsNumber;

    @ApiModelProperty(notes = "当前催收员")
    private String currentCollector;

    @ApiModelProperty(notes = "协助催收员")
    private String assistCollector;

    @ApiModelProperty("申请原因")
    private String applyReason;

    @ApiModelProperty(notes = "协助标识")
    private AssistFlag assistFlag;

    @ApiModelProperty(notes = "协助类型")
    private AssistFlag assistOutType;

    @ApiModelProperty("协催地址")
    private String addressDetail;

    @ApiModelProperty("地址类型")
    private String addressType;

    @ApiModelProperty("关系")
    private String relation;

    @ApiModelProperty("地址Id")
    private String personalAddressId;

    @ApiModelProperty("联系电话Id")
    private String personalContactId;
}
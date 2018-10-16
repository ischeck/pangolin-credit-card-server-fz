package cn.fintecher.pangolin.service.domain.model;

import cn.fintecher.pangolin.common.enums.AssistFlag;
import cn.fintecher.pangolin.common.enums.AssistStatus;
import cn.fintecher.pangolin.entity.domain.CardInformation;
import cn.fintecher.pangolin.entity.domain.Personal;
import cn.fintecher.pangolin.entity.managentment.Operator;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.Set;

/**
 * @Author : huyanmin
 * @Description : 协催案件查询model
 * @Date : 2018/8/7.
 */
@Data
public class AssistCollectionCaseSearchModel {

    @ApiModelProperty("案件Id")
    private String Id;

    @ApiModelProperty("批次号")
    private String batchNumber;

    @ApiModelProperty("姓名")
    private String personalName;

    @ApiModelProperty("催收对象名称")
    private String targetName;

    @ApiModelProperty("证件号")
    private String idCard;

    @ApiModelProperty("委托方名称")
    private String principalName;

    @ApiModelProperty("委托方Id")
    private String principalId;

    @ApiModelProperty("卡号")
    private Set<CardInformation> cardInformationSet;

    @ApiModelProperty(notes = "案件Id")
    private String caseId;

    @ApiModelProperty(notes = "协助标识")
    private AssistFlag assistFlag;

    @ApiModelProperty(notes = "协助状态")
    private AssistStatus assistStatus;

    @ApiModelProperty("协催地址")
    private String addressDetail;

    @ApiModelProperty("地址类型")
    private String addressType;

    @ApiModelProperty("关系")
    private String relation;

    @ApiModelProperty(notes = "当前电话协助催收员")
    private Operator currentCollector;

    @ApiModelProperty("申请原因")
    private String applyReason;

    @ApiModelProperty(notes = "外访时间")
    private Date applyDate;

    @ApiModelProperty("申请人姓名")
    private String applyRealName;

}

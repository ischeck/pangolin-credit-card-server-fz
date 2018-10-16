package cn.fintecher.pangolin.service.domain.model.response;

import cn.fintecher.pangolin.entity.managentment.Principal;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.Set;

/**
 * @Author : huyanmin
 * @Description : 案件查询结果
 * @Date : 2018/7/30.
 */
@Data
public class PublicCaseSearchResponse {

    @ApiModelProperty("公共案件Id")
    private String publicId;

    @ApiModelProperty("案件Id")
    private String caseId;

    @ApiModelProperty("批次号")
    private String batchNumber;

    @ApiModelProperty("姓名")
    private String  personalName;

    @ApiModelProperty(notes = "移动电话")
    private String selfPhoneNo;

    @ApiModelProperty("委托方")
    private Principal principal;

    @ApiModelProperty(notes = "证件号")
    private String certificateNo;

    @ApiModelProperty(notes = "案件状态")
    private Set<String> caseStatus;

    @ApiModelProperty(notes = "催收状态")
    private Set<String> collectionStatus;

    @ApiModelProperty(notes = "余额")
    private Double leftAmt;

    @ApiModelProperty(notes = "账号")
    private String account;

    @ApiModelProperty(notes = "案件编号")
    private String caseNumber;

    @ApiModelProperty(notes = "手数")
    private String handsNumber;

    @ApiModelProperty(notes = "美元余额")
    private Double leftAmtDollar;

    @ApiModelProperty(notes = "跟进时间")
    private Date followTime;

    @ApiModelProperty(notes = "地区")
    private String city;

    @ApiModelProperty(notes = "委案日期")
    @Field(type = FieldType.Date)
    private Date delegationDate;

    @ApiModelProperty(notes = "结案日期")
    @Field(type = FieldType.Date)
    private Date endCaseDate;

    @ApiModelProperty(notes = "催计数")
    @Field(type = FieldType.Integer)
    private Integer collectionRecordCount;

}

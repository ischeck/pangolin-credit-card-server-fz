package cn.fintecher.pangolin.service.domain.model;

import cn.fintecher.pangolin.common.enums.AssistFlag;
import cn.fintecher.pangolin.common.enums.Source;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @Author : huyanmin
 * @Description : 联系人电话
 * @Date : 2018/8/3.
 */
@Data
public class PersonalContactSearchModel {

    @Id
    private String id;

    @ApiModelProperty(notes = "联系人电话Id")
    private String personalPerId;

    @ApiModelProperty(notes = "联系人姓名")
    private String name;

    @ApiModelProperty(notes = "联系人身份证号码")
    private String certificateNo;

    @ApiModelProperty(notes = "关系")
    private String relation;

    @ApiModelProperty(notes = "电话类型")
    private String phoneType;

    @ApiModelProperty(notes = "电话号码")
    private String phoneNo;

    @ApiModelProperty(notes = "拨打电话次数")
    private Integer dialPhoneCount = 0;

    @ApiModelProperty(notes = "拨打电话次数")
    private Integer sendMessageCount = 0;

    @ApiModelProperty("协催类型")
    private AssistFlag assistContactFlag;

    @ApiModelProperty(notes = "联络结果")
    private String contactResult;

    @ApiModelProperty(notes = "电话状态")
    private String phoneState;

    @ApiModelProperty("协催标识")
    private AssistFlag assistFlag;

    @ApiModelProperty(notes = "备注")
    private String remark;

    @ApiModelProperty(notes = "来源")
    private Source source;

    @ApiModelProperty(notes = "联系人排序")
    private Integer sort;
}

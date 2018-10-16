package cn.fintecher.pangolin.service.domain.model;

import cn.fintecher.pangolin.common.enums.AssistFlag;
import cn.fintecher.pangolin.common.enums.Source;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author : huyanmin
 * @Description : 联系人地址
 * @Date : 2018/8/3.
 */
@Data
public class PersonalAddressSearchModel {

    @ApiModelProperty(notes = "联系人ID")
    private String id;

    @ApiModelProperty(notes = "联系人ID")
    private String personalAddressId;

    @ApiModelProperty(notes = "联系人身份证号码")
    private String certificateNo;

    @ApiModelProperty(notes = "联系人姓名")
    private String name;

    @ApiModelProperty(notes = "关系")
    private String relation;

    @ApiModelProperty(notes = "详细地址")
    private String addressDetail;

    @ApiModelProperty(notes = "地址类型")
    private String addressType;

    @ApiModelProperty(notes = "地址状态")
    private String addressState;

    @ApiModelProperty("协催标识")
    private AssistFlag assistAddressFlag;

    @ApiModelProperty("协催标识")
    private AssistFlag assistFlag;

    @ApiModelProperty("来源")
    private Source source;

    @ApiModelProperty("备注")
    private String remark;
}

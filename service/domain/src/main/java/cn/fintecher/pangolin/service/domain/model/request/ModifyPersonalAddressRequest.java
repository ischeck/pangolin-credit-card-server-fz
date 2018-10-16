package cn.fintecher.pangolin.service.domain.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author : huyanmin
 * @Description : 修改联系人地址
 * @Date : 2018/9/27.
 */
@Data
public class ModifyPersonalAddressRequest {

    @ApiModelProperty(notes = "联系人id")
    private String id;

    @ApiModelProperty(notes = "地址id")
    private String personalAddressId;

    @ApiModelProperty(notes = "联系人姓名")
    private String name;

    @ApiModelProperty(notes = "身份证号码")
    private String certificateNo;

    @ApiModelProperty(notes = "关系")
    private String relation;

    @ApiModelProperty(notes = "详细地址")
    private String addressDetail;

    @ApiModelProperty(notes = "地址类型")
    private String addressType;

    @ApiModelProperty("备注")
    private String remark;

}

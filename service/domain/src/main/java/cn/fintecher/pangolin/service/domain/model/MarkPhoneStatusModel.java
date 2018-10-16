package cn.fintecher.pangolin.service.domain.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author : huyanmin
 * @Description : 电话状态查询
 * @Date : 2018/9/27.
 */
@Data
public class MarkPhoneStatusModel {

    @ApiModelProperty(notes = "联系人ID")
    private String personalContactId;

    @ApiModelProperty(notes = "电话状态")
    private String phoneState;

    @ApiModelProperty(notes = "电话号码")
    private String phoneNo;

    @ApiModelProperty(notes = "地址状态")
    private String addressState;

    @ApiModelProperty(notes = "详细地址")
    private String addressDetail;

}

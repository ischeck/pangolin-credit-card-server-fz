package cn.fintecher.pangolin.service.domain.model.response;

import cn.fintecher.pangolin.common.enums.AssistFlag;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AppBaseInfoResponse {
    @ApiModelProperty("姓名")
    private String personalName;

    @ApiModelProperty(notes = "性别")
    private String sex;

    @ApiModelProperty("电话")
    private String phone;

    @ApiModelProperty(notes = "证件号")
    private String certificateNo;

    @ApiModelProperty(notes = "联系人")
    private List<AppPersonalContactModel> contacts;

    @ApiModelProperty(notes = "地址")
    private AppPersonalAddressModel address;
}

package cn.fintecher.pangolin.service.domain.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class AppPersonalAddressModel {

    @ApiModelProperty(notes = "联系人姓名")
    private String contactName;

    @ApiModelProperty(notes = "关系")
    private String relation;

    @ApiModelProperty(notes = "详细地址")
    private String addressDetail;

    @ApiModelProperty(notes = "地址类型")
    private String addressType;

    @ApiModelProperty(notes = "地址状态")
    private String addressState;

    @ApiModelProperty("来源")
    private String source;

    @ApiModelProperty(notes = "操作时间")
    private Date operatorTime;
}

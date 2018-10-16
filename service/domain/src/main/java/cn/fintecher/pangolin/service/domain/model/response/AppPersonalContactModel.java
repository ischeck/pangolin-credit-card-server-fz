package cn.fintecher.pangolin.service.domain.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class AppPersonalContactModel {
    @ApiModelProperty(notes = "联系人姓名")
    private String name;

    @ApiModelProperty(notes = "关系")
    private String relation;

    @ApiModelProperty(notes = "电话号码")
    private String phoneNo;

    @ApiModelProperty(notes = "电话状态")
    private String phoneState;

    @ApiModelProperty(notes = "操作时间")
    private Date operatorTime;
}

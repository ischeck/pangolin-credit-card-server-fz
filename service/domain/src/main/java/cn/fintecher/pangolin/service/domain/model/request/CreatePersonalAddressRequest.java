package cn.fintecher.pangolin.service.domain.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @Author : huyanmin
 * @Description : 新增联系人地址
 * @Date : 2018/8/3.
 */
@Data
public class CreatePersonalAddressRequest {

    @ApiModelProperty(notes = "联系人姓名")
    private String name;

    @ApiModelProperty(notes = "身份证号码")
    private String certificateNo;

    @ApiModelProperty(notes = "客户Id")
    private String personalId;

    @ApiModelProperty(notes = "关系")
    private String relation;

    @ApiModelProperty(notes = "详细地址")
    private String addressDetail;

    @ApiModelProperty(notes = "地址类型")
    private String addressType;

    @ApiModelProperty("来源")
    private String source;

    @ApiModelProperty("备注")
    private String remark;
}

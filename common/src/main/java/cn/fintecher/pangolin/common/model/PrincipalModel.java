package cn.fintecher.pangolin.common.model;

import cn.fintecher.pangolin.common.enums.BusinessType;
import cn.fintecher.pangolin.common.enums.PrincipalState;
import cn.fintecher.pangolin.common.enums.PrincipalType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;
import java.util.Date;

/**
 *
 * @Author huyanmin
 * @Date 2018/06/26
 * @Dessciption 委托方返回model
 */
@Data
public class PrincipalModel {

    @ApiModelProperty("委托方ID")
    private String id;

    @ApiModelProperty("委托方名称")
    private String principalName;

    @ApiModelProperty(notes = "手机号")
    private String phone;

    @ApiModelProperty(notes = "备注")
    private String remark;

    @ApiModelProperty(notes = "创建时间")
    private Date operatorTime;

    @ApiModelProperty(notes = "是否删除 启用/禁用")
    private PrincipalState state;

}

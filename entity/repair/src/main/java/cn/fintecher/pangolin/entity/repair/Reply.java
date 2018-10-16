package cn.fintecher.pangolin.entity.repair;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author : hanwannan
 * @Description : 数据修复-申调回复
 * @Date : 2018/8/28.
 */
@Data
@ApiModel(value = "Reply", description = "申调回复")
public class Reply {

    @ApiModelProperty(notes = "文件")
    private String file;

    @ApiModelProperty("与本人关系")
    private String relationship;

    @ApiModelProperty("证件类型")
    private String type;

    @ApiModelProperty("备注")
    private String remark;
}

package cn.fintecher.pangolin.entity.repair;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author : hanwannan
 * @Description : 数据修复-证件信息
 * @Date : 2018/8/28.
 */
@Data
@ApiModel(value = "Credential", description = "证件信息")
public class Credential {

    @ApiModelProperty(notes = "文件ID")
    private String fileId;

    @ApiModelProperty("与本人关系")
    private String relationship;

    @ApiModelProperty("证件类型")
    private String type;

    @ApiModelProperty("备注")
    private String remark;
}

package cn.fintecher.pangolin.entity.managentment;

import cn.fintecher.pangolin.common.model.SysParamModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author : huyanmin
 * @Description : 系统参数表
 * @Date : 2018/6/27.
 */
@Data
@Document
@ApiModel(value = "SysParam", description = "系统参数")
public class SysParam extends SysParamModel{

    @Id
    @ApiModelProperty(notes = "id")
    private String id;
}

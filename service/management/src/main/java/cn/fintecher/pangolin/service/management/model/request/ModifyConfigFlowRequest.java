package cn.fintecher.pangolin.service.management.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author huyanmin
 * @Date 2018/07/24
 * @Dessciption 创建配置流程参数
 */
@Data
public class ModifyConfigFlowRequest extends CreateConfigFlowRequest {

    @ApiModelProperty(notes = "id")
    private String id;

}

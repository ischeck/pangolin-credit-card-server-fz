package cn.fintecher.pangolin.service.management.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by huyanmin on 2018/6/27
 */
@Data
public class ModifyDistributedRequest {

    @ApiModelProperty("用户ID")
    private List<String> operatorId;

    @ApiModelProperty("角色ID")
    private List<String> roleId;
}

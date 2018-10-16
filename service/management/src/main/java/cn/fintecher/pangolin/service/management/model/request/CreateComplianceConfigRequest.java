package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.model.PrincipalModel;
import cn.fintecher.pangolin.common.model.ResourceModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Created by ChenChang on 2018/8/31.
 */
@Data
public class CreateComplianceConfigRequest {

    @ApiModelProperty("配置名称")
    @NotNull(message = "{complianceConfig.name.is.required}")

    private String name;
    @ApiModelProperty("对应的组织")
    @NotNull(message = "{org.is.required}")
    private String organizationId;

    @ApiModelProperty("允许的委托方")
    private Set<PrincipalModel> enablePrincipals;

    @ApiModelProperty("要关闭的功能")
    private Set<ResourceModel> disableResources;
}

package cn.fintecher.pangolin.service.management.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Created by ChenChang on 2018/8/31.
 */
@Data
public class ModifyAllowedPrincipals {
    @ApiModelProperty("配置ID")
    @NotNull(message = "{id.is.required}")
    private String complianceConfigId;
    @ApiModelProperty("选中允许的委托方ID列表")
    private Set<String> principalIds;
}

package cn.fintecher.pangolin.service.management.model;

import cn.fintecher.pangolin.common.enums.ComplianceState;
import cn.fintecher.pangolin.common.model.PrincipalModel;
import cn.fintecher.pangolin.common.model.ResourceModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.Set;

/**
 * Created by ChenChang on 2018/8/31.
 */
@Data
public class ComplianceConfigListModel {
    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty("配置名称")
    private String name;

    @ApiModelProperty("对应的组织ID")
    private String organizationId;

    @ApiModelProperty("对应的组织名称")
    private String organizationName;

    @ApiModelProperty("允许的委托方")
    private Set<PrincipalModel> enablePrincipals;

    @ApiModelProperty("要关闭的功能")
    private Set<ResourceModel> disableResources;

    @ApiModelProperty("状态")
    private ComplianceState state;

    @ApiModelProperty("创建人名称")
    private String operatorName;

    @ApiModelProperty("创建时间")
    private Date createTime;
}

package cn.fintecher.pangolin.service.management.model;

import cn.fintecher.pangolin.common.enums.ComplianceState;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.model.OrganizationModel;
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
public class ComplianceConfigModel {
    private String id;

    @ApiModelProperty("配置名称")
    private String name;

    @ApiModelProperty("允许的委托方")
    private Set<PrincipalModel> enablePrincipals;

    @ApiModelProperty("对应的组织")
    private OrganizationModel organization;

    @ApiModelProperty("要关闭的功能")
    private Set<ResourceModel> disableResources;

    @ApiModelProperty("状态")
    private ComplianceState state;

    @ApiModelProperty("创建人")
    private OperatorModel operator;

    @ApiModelProperty("创建时间")
    private Date createTime;
}

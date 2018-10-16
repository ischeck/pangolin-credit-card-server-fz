package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.enums.ApplyType;
import cn.fintecher.pangolin.common.enums.OrganizationApproveType;
import cn.fintecher.pangolin.common.model.ConfigFlowModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author huyanmin
 * @Date 2018/07/24
 * @Dessciption 创建配置流程参数
 */
@Data
public class CreateConfigFlowRequest {

    @ApiModelProperty(notes = "配置名称")
    private String configName;

    @ApiModelProperty(notes = "申请类型")
    private ApplyType configType;

    @ApiModelProperty(notes = "审批级数")
    private Integer level;

    @ApiModelProperty(notes = "机构审批类型")
    private OrganizationApproveType organizationApproveType;

    @ApiModelProperty(notes = "审批角色流程")
    private Map<Integer, ConfigFlowModel> configMap;

}

package cn.fintecher.pangolin.common.model;

import cn.fintecher.pangolin.common.enums.ApplyType;
import cn.fintecher.pangolin.common.enums.OrganizationApproveType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 审批流程设置
 * Created by huyanmin 2018/07/16
 */

@Data
public class ApproveFlowConfigModel {
    @Id
    private String id;

    @ApiModelProperty(notes = "配置名称")
    private String configName;

    @ApiModelProperty(notes = "申请类型")
    private ApplyType configType;

    @ApiModelProperty(notes = "审批级数")
    private Integer level;

    @ApiModelProperty(notes = "审批角色流程")
    private Map<Integer, ConfigFlowModel> configMap;

    @ApiModelProperty(notes = "机构审批类型")
    private OrganizationApproveType organizationApproveType;

    @ApiModelProperty(notes = "操作人信息")
    private String operator;

    @ApiModelProperty(notes = "操作时间")
    private Date operatorTime;
}

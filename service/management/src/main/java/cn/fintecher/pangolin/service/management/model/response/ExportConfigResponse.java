package cn.fintecher.pangolin.service.management.model.response;

import cn.fintecher.pangolin.common.enums.ExportType;
import cn.fintecher.pangolin.common.enums.TemplateType;
import cn.fintecher.pangolin.entity.managentment.ExportConfigItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author:BBG
 * @Desc: Excel 模板导出返回信息
 * @Date:Create in 19:37 2018/7/23
 */
@Data
public class ExportConfigResponse implements Serializable {

    private String id;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("委托方")
    private String principalId;

    @ApiModelProperty("类型")
    private ExportType exportType;

    @ApiModelProperty("委托方名称")
    private String principalName;

    @ApiModelProperty("配置项")
    private List<ExportConfigItem> items;

    @ApiModelProperty("创建人")
    private String operator;

    @ApiModelProperty("创建人名称")
    private String operatorName;

    @ApiModelProperty("创建时间")
    private Date createTime;

}

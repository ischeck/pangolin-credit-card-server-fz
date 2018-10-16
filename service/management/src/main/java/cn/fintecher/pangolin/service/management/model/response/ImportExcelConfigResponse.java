package cn.fintecher.pangolin.service.management.model.response;

import cn.fintecher.pangolin.common.enums.TemplateType;
import cn.fintecher.pangolin.entity.managentment.Principal;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author:peishouwen
 * @Desc: Excel 模板导入返回信息
 * @Date:Create in 19:37 2018/7/23
 */
@Data
public class ImportExcelConfigResponse implements Serializable {

    @ApiModelProperty("模板ID")
    private String id;

    @ApiModelProperty("模板名称")
    public String name;

    @ApiModelProperty("委托方ID")
    public String principalId;

    @ApiModelProperty("委托方名称")
    public String principalName;

    @ApiModelProperty("模板类型")
    private TemplateType templateType;
    @ApiModelProperty("创建人")
    private String operator;

    @ApiModelProperty("创建时间")
    private Date createTime;

}

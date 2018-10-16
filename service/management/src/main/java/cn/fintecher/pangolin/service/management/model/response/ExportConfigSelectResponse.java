package cn.fintecher.pangolin.service.management.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author:BBG
 * @Desc: Excel 模板导出返回信息
 * @Date:Create in 19:37 2018/7/23
 */
@Data
public class ExportConfigSelectResponse implements Serializable {

    private String id;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("委托方名称")
    private String principalName;
}
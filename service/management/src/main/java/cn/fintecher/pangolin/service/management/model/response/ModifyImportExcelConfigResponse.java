package cn.fintecher.pangolin.service.management.model.response;

import cn.fintecher.pangolin.common.enums.TemplateType;
import cn.fintecher.pangolin.entity.managentment.ImportExcelConfigItem;
import cn.fintecher.pangolin.entity.managentment.Principal;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.List;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 16:40 2018/7/25
 */
@Data
public class ModifyImportExcelConfigResponse implements Serializable{

    private String id;
    @ApiModelProperty("配置名称")
    private String name;

    @ApiModelProperty("模板类型")
    private TemplateType templateType;

    @ApiModelProperty("委托方")
    private Principal principal;

    @ApiModelProperty("表头开始行")
    private Integer  titleStartRow;

    @ApiModelProperty("表头开始列")
    private Integer  titleStartCol;

    @ApiModelProperty("数据开始行")
    private Integer  dataStartRow;

    @ApiModelProperty("数据开始列")
    private Integer  dataStartCol;

    @ApiModelProperty("sheet页总数")
    private Integer sheetTotals;

    @ApiModelProperty("配置项")
    private List<ImportExcelConfigItem> items;
}

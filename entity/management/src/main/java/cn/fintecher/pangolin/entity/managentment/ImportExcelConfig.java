package cn.fintecher.pangolin.entity.managentment;

import cn.fintecher.pangolin.common.enums.TemplateType;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 导入配置
 * Created by ChenChang on 2017/12/23.
 */
@Data
@Document
public class ImportExcelConfig implements Serializable {
    @Id
    private String id;
    @ApiModelProperty("配置名称")
    private String name;

    @ApiModelProperty("模板类型")
    private TemplateType templateType;

    @ApiModelProperty("委托方ID")
    public String principalId;

    @ApiModelProperty("委托方名称")
    public String principalName;

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

    @ApiModelProperty("创建人")
    private String operator;

    @ApiModelProperty("创建时间")
    private Date createTime;


}

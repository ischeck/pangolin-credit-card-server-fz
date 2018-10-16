
package cn.fintecher.pangolin.entity.managentment;

import cn.fintecher.pangolin.common.enums.ExportType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document
public class ExportConfig {

    @Id
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

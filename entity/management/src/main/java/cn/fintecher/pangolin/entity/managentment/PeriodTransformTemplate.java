package cn.fintecher.pangolin.entity.managentment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
@Document
@ApiModel("期数转换模板")
public class PeriodTransformTemplate implements Serializable {
    @Id
    private String id;

    @ApiModelProperty("委托方ID")
    private String principalId;

    @ApiModelProperty(notes = "委托方名称")
    private String principalName;

    @ApiModelProperty("转换规则")
    private Map<String, String> tramsformMap;

    @ApiModelProperty("操作人名称")
    private String operatorName;

    @ApiModelProperty("操作时间")
    private Date operatorTime;
}

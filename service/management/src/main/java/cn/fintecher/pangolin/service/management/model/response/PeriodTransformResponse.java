package cn.fintecher.pangolin.service.management.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.Map;

@Data
public class PeriodTransformResponse {
    @Id
    private String id;

    @ApiModelProperty("委托方ID")
    private String principalId;

    @ApiModelProperty("委托方名称")
    private String principalName;

    @ApiModelProperty("转换规则")
    private Map<String, String> tramsformMap;

    @ApiModelProperty("操作时间")
    private Date operatorTime;

    @ApiModelProperty("操作人名称")
    private String operatorName;
}

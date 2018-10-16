package cn.fintecher.pangolin.service.management.model.response;

import cn.fintecher.pangolin.common.enums.SensitiveLevel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;

@Data
public class SensitiveWordResponse {
    @Id
    private String id;

    @ApiModelProperty("委托方ID")
    private String principalId;

    @ApiModelProperty("委托方名称")
    private String principalName;

    @ApiModelProperty("词汇")
    private String word;

    @ApiModelProperty("级别")
    private SensitiveLevel level;

    @ApiModelProperty("操作时间")
    private Date operatorTime;

    @ApiModelProperty("操作人名称")
    private String operatorName;
}

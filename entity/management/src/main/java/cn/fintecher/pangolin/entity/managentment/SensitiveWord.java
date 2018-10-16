package cn.fintecher.pangolin.entity.managentment;

import cn.fintecher.pangolin.common.enums.SensitiveLevel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Document
@ApiModel(value = "敏感词")
public class SensitiveWord implements Serializable {
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

    @ApiModelProperty("操作人名称")
    private String operatorName;

    @ApiModelProperty("操作时间")
    private Date operatorTime;
}

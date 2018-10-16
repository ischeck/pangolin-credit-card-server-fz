package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.enums.SensitiveLevel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ModifySensitiveWordRequest {
    private String id;

    @ApiModelProperty("词汇")
    private String word;

    @ApiModelProperty("级别")
    private SensitiveLevel level;

}

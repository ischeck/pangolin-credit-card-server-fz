package cn.fintecher.pangolin.service.management.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class ModifyPeriodTransformRequest {
    private String id;

    @ApiModelProperty("转换规则")
    private Map<String, String> tramsformMap;

}

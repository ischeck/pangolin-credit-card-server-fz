package cn.fintecher.pangolin.service.management.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by huyanmin on 2018/8/22.
 */
@Data
public class ModifyContactResultRequest extends CreateContactResultRequest {

    @ApiModelProperty("id")
    private String id;

}

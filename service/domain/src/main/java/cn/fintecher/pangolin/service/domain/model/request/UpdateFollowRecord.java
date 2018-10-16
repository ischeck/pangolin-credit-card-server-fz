package cn.fintecher.pangolin.service.domain.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by huyanmin on 2018/8/8.
 */
@Data
public class UpdateFollowRecord {

    @ApiModelProperty("跟进记录id")
    private String id;

    @ApiModelProperty("备注")
    private String remark;

}

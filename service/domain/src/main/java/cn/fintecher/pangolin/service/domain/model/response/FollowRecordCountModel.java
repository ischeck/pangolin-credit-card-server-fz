package cn.fintecher.pangolin.service.domain.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by BBG on 2018/8/7.
 */
@Data
public class FollowRecordCountModel {

    @ApiModelProperty("电话次数")
    private Long telNum = 0L;

    @ApiModelProperty("地址次数")
    private Long addrNum = 0L;

    @ApiModelProperty("查找次数")
    private Long findNum = 0L;

    @ApiModelProperty("信函次数")
    private Long letterNum = 0L;
}

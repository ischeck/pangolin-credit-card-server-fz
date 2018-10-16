package cn.fintecher.pangolin.service.domain.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author : huyanmin
 * @Description : 留案请求
 * @Date : 2018/8/7.
 */
@Data
@ApiModel(value = "LeaveCaseRequest", description = "留案请求")
public class LeaveCaseRequest {

    @ApiModelProperty("案件的ID")
    private String id;

    @ApiModelProperty("申请原因")
    private String leaveReason;
}

package cn.fintecher.pangolin.service.dataimp.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 14:01 2018/7/29
 */
@Data
public class BatchNumberResponse implements Serializable{
    @ApiModelProperty(notes = "批次号")
    private String batchNumber;
}

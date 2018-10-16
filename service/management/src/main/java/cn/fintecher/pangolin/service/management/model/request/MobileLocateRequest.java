package cn.fintecher.pangolin.service.management.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("APP定位请求")
public class MobileLocateRequest {

    @ApiModelProperty("经度")
    private String longitude;

    @ApiModelProperty("维度")
    private String latitude;

    @ApiModelProperty("地址")
    private String address;

}

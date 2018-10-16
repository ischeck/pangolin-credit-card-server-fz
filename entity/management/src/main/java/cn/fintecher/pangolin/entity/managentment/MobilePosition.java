package cn.fintecher.pangolin.entity.managentment;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Data
@Document
@ApiModel(value = "定位信息")
public class MobilePosition implements Serializable {

    @Id
    private String id;

    @ApiModelProperty("用户ID")
    private String operator;

    @ApiModelProperty("用户名称")
    private String operatorName;

    @ApiModelProperty("机构")
    private String organization;

    @ApiModelProperty("经度")
    private String longitude;

    @ApiModelProperty("维度")
    private String latitude;

    @ApiModelProperty("地址")
    private String address;

    @ApiModelProperty("时间")
    private Date date;
}

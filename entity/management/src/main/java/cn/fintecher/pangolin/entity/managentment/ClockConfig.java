package cn.fintecher.pangolin.entity.managentment;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

@Data
@Document
@ApiModel("打卡设置")
public class ClockConfig implements Serializable {
    @Id
    private String id;

    @ApiModelProperty("所属机构")
    private String organization;

    @ApiModelProperty("公司名称")
    private String organizationName;

    @ApiModelProperty("打卡配置")
    private List<ClockConfigDetail> clockConfigDetails;
}

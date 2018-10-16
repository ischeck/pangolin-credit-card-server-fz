package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.enums.ClockType;
import cn.fintecher.pangolin.entity.managentment.ClockConfigDetail;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CreateClockConfigRequest {

    @ApiModelProperty("所属机构")
    private String organization;

    @ApiModelProperty("公司名称")
    private String organizationName;

    @ApiModelProperty("打卡配置")
    private List<ClockConfigDetail> clockConfigDetails;

}

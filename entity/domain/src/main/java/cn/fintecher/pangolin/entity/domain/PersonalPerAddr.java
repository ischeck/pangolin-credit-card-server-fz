package cn.fintecher.pangolin.entity.domain;

import cn.fintecher.pangolin.common.enums.AssistFlag;
import cn.fintecher.pangolin.common.enums.Source;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 15:38 2018/9/6
 */
@Data
@ApiModel(value = "PersonalPerAddr", description = "联系人地址子项")
public class PersonalPerAddr {

    @Id
    @ApiModelProperty(notes = "id")
    private String id;

    @ApiModelProperty(notes = "地址类型")
    private String addressType;

    @ApiModelProperty(notes = "地址状态")
    private String addressState;

    @ApiModelProperty(notes = "详细地址")
    private String addressDetail;

    @ApiModelProperty("协催标识")
    private AssistFlag assistAddressFlag=AssistFlag.NO_ASSIST;

    @ApiModelProperty("来源")
    private Source source;

    @ApiModelProperty(notes = "备注")
    private String remark;

}

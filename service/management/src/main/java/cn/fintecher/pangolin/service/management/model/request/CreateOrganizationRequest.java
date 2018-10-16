package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.enums.CollectionOrganization;
import cn.fintecher.pangolin.common.enums.OrganizationType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by ChenChang on 2018/6/8.
 */
@Data
public class CreateOrganizationRequest {

    @ApiModelProperty(notes = "机构名称")
    @NotNull(message = "{name.is.required}")
    private String name;
    @ApiModelProperty("机构电话")
    private String phone;
    @ApiModelProperty("联系人")
    private String contactPerson;
    @ApiModelProperty(notes = "地址详细文字")
    private String addressText;
    @NotNull(message = "{org.parent.is.required}")
    @ApiModelProperty("父机构")
    private String parent;
    @ApiModelProperty("机构类型")
    private OrganizationType type;
    @ApiModelProperty("是否是催收结构")
    private CollectionOrganization collectionOrganization;
    @ApiModelProperty("机构级别")
    private Integer level;
}

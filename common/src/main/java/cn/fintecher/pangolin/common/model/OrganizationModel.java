package cn.fintecher.pangolin.common.model;

import cn.fintecher.pangolin.common.enums.CollectionOrganization;
import cn.fintecher.pangolin.common.enums.OrganizationType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by ChenChang on 2018/6/8.
 */
@Data
public class OrganizationModel implements Serializable{
    private String id;
    @ApiModelProperty(notes = "机构名称")
    private String name;
    @ApiModelProperty("机构电话")
    private String phone;
    @ApiModelProperty("联系人")
    private String contactPerson;
    @ApiModelProperty("等级")
    private Integer level;
    @ApiModelProperty(notes = "地址详细文字")
    private String addressText;
    @ApiModelProperty("创建时间")
    private Date createTime;
    @ApiModelProperty("机构code")
    private String departmentCode;
    @ApiModelProperty("操作者")
    private String operator;
    @ApiModelProperty("父机构")
    private String parent;
    @ApiModelProperty("机构类型")
    private OrganizationType type;
    @ApiModelProperty("是否是催收结构")
    private CollectionOrganization collectionOrganization;
}

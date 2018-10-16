package cn.fintecher.pangolin.entity.managentment;

import cn.fintecher.pangolin.common.enums.CollectionOrganization;
import cn.fintecher.pangolin.common.enums.OperatorState;
import cn.fintecher.pangolin.common.enums.OrganizationType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * 组织机构
 * Created by ChenChang on 2017/12/12.
 */
@Data
@Document
public class Organization implements Serializable {
    @Id
    private String id;
    @ApiModelProperty(notes = "机构名称")
    private String name;
    @ApiModelProperty("机构电话")
    private String phone;
    @ApiModelProperty("联系人")
    private String contactPerson;
    @ApiModelProperty(notes = "地址详细文字")
    private String addressText;
    @ApiModelProperty("创建时间")
    private Date createTime;
    @ApiModelProperty("操作者")
    private String operator;
    @ApiModelProperty("父机构")
    private String parent;
    @ApiModelProperty("机构code")
    private String departmentCode;
    @ApiModelProperty("机构类型")
    private OrganizationType type;
    @ApiModelProperty("是否是催收结构")
    private CollectionOrganization collectionOrganization;
    @ApiModelProperty("机构状态")
    private OperatorState state;
    @ApiModelProperty("机构级别")
    private Integer level=new Integer(0);
    @ApiModelProperty("打卡配置")
    private String clockConfigId;
}

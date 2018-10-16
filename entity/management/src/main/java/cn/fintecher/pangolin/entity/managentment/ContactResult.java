package cn.fintecher.pangolin.entity.managentment;

import cn.fintecher.pangolin.common.enums.ConfigState;
import cn.fintecher.pangolin.common.enums.ManagementType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * Created by BBG on 2018/8/3.
 */

@Data
@Document
public class ContactResult implements Serializable {
    @Id
    private String id;

    @ApiModelProperty("PID")
    private String pid;

    @ApiModelProperty("CODE")
    private String code;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("级别")
    private Integer level;

    @ApiModelProperty("是否可扩展")
    private ManagementType isExtension;

    @ApiModelProperty("委托方ID")
    private String principalId;

    @ApiModelProperty("委托方名称")
    private String principalName;

    @ApiModelProperty("属性名")
    private String attribute;

    @ApiModelProperty("属性类型")
    private String propertyType;

    @ApiModelProperty("是否必输")
    private String isMustInput;

    @ApiModelProperty("状态-disable的数据是用于每添加一个委托方时，新增相应的字段")
    private ConfigState configState;
}

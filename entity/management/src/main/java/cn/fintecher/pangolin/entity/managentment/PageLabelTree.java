package cn.fintecher.pangolin.entity.managentment;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author:peishouwen
 * @Desc: 页面标签配置树形实体
 * @Date:Create in 10:44 2018/7/20
 */
@Data
@Document
public class PageLabelTree implements Serializable {
    @Id
    private String id;

    @ApiModelProperty("父ID")
    private String pid;

    @ApiModelProperty("元素级别")
    private Integer level;

    @ApiModelProperty("配置页面标签ID")
    private String pageLabelConfigId;

    @ApiModelProperty("元素名称")
    private String name;

    @ApiModelProperty("元素码")
    private String code;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("创建人")
    private String operator;

    @ApiModelProperty("操作时间")
    private Date operatorTime;
}

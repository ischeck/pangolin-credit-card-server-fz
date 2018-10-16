package cn.fintecher.pangolin.common.model;

import cn.fintecher.pangolin.common.enums.ResourceType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by huyanmin on 2018/7/23
 */
@Data
public class ResourceModel implements Serializable{

    private Long id;
    @ApiModelProperty(notes = "资源名称")
    private String name;
    @ApiModelProperty(notes = "层级")
    private Integer level;
    @ApiModelProperty("类型")
    private ResourceType type;
    @ApiModelProperty("地址")
    private String url;
    @ApiModelProperty("图标")
    private String icon;
    @ApiModelProperty("父功能ID")
    private Long parent;
    @ApiModelProperty("排序")
    private Integer sort;

}

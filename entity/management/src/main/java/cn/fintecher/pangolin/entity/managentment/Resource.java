package cn.fintecher.pangolin.entity.managentment;

import cn.fintecher.pangolin.common.enums.ResourceType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * Created by ChenChang on 2017/12/13.
 */
@Data
@Document
public class Resource implements Serializable {

    @Id
    private String id;
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
    private String parent;
    @ApiModelProperty("排序")
    private Integer sort;

}

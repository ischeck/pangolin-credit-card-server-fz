package cn.fintecher.pangolin.service.management.model.response;

import cn.fintecher.pangolin.common.enums.ResourceType;
import cn.fintecher.pangolin.entity.managentment.Resource;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * Created by ChenChang on 2018/6/7
 */
@Data
public class ResourceResponse {

    @Id
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

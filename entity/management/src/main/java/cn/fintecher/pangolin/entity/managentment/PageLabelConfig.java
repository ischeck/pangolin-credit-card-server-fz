package cn.fintecher.pangolin.entity.managentment;

import cn.fintecher.pangolin.common.enums.LabelType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author:peishouwen
 * @Desc: 页面配置
 * @Date:Create in 10:03 2018/7/20
 */
@Data
@Document
public class PageLabelConfig implements Serializable {
    @Id
    private String id;

    @ApiModelProperty("标签名称")
    private String name;

    @ApiModelProperty("标签类型")
    private LabelType type;

    @ApiModelProperty("占用行数")
    private Integer occupiedLines;

    @ApiModelProperty("占用列数")
    private Integer occupiedCols;

    @ApiModelProperty("是否必输")
    private Boolean isRequired;

    @ApiModelProperty("是否合并")
    private Boolean isMerge;

    @ApiModelProperty("是否多选")
    private boolean mulSelect;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("所属委托方")
    private Principal principal;

    @ApiModelProperty("创建人")
    private String operator;

    @ApiModelProperty("操作时间")
    private Date operatorTime;

}

package cn.fintecher.pangolin.entity.managentment;

import cn.fintecher.pangolin.common.enums.EnumMessage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * Created by BBG on 2018/8/8.
 */
@Data
@Document
public class Color implements Serializable {
    @Id
    private String id;

    @ApiModelProperty("颜色")
    private String name;

    @ApiModelProperty("色值")
    private String value;
}

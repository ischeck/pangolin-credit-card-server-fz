package cn.fintecher.pangolin.entity.managentment;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 委托方自定义字典配置
 * Created by ChenChang on 2018/7/16.
 */
@Data
@Document
public class PrincipalDictConfig {
    @Id
    @ApiModelProperty(notes = "id标识")
    private String id;
    @ApiModelProperty(notes = "对应委托方ID")
    private String principal;
    @ApiModelProperty(notes = "对应枚举类型")
    private String enumType;
    @ApiModelProperty(notes = "对应枚举值")
    private String code;
    @ApiModelProperty(notes = "自定义显示值")
    private String value;
}

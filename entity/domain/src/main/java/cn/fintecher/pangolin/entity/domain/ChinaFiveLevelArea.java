package cn.fintecher.pangolin.entity.domain;


import cn.fintecher.pangolin.common.enums.AreaType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import static org.springframework.data.elasticsearch.annotations.FieldType.text;

/**
 * Created by ChenChang on 2018/7/17.
 */
@Data
@Document(indexName = "china_five_level_area", type = "china_five_level_area", shards = 1, replicas = 0)
@ApiModel(value = "chinaFiveLevelArea", description = "中国五级区域名称和编码")
public class ChinaFiveLevelArea {
    @Id
    @ApiModelProperty(notes = "id")
    private String id;
    @ApiModelProperty(notes = "编码 区编码可查身份证")
    @Field(type = text, store = true, fielddata = true)
    private String code;
    @ApiModelProperty(notes = "名字")
    @Field(type = text, store = true, fielddata = true)
    private String name;
    @ApiModelProperty(notes = "省市县乡村")
    private AreaType type;
    @ApiModelProperty(notes = "父")
    private ChinaFiveLevelArea parent;
}

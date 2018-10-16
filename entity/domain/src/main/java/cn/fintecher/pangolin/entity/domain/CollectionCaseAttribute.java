package cn.fintecher.pangolin.entity.domain;

import cn.fintecher.pangolin.common.enums.AttributeType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * 案件属性
 * Created by ChenChang on 2017/12/22.
 */
@Data
@Document(indexName = "order_attribute", type = "order_attribute", shards = 1, replicas = 0)
@ApiModel(value = "CollectionCaseAttribute", description = "案件属性")
public class CollectionCaseAttribute {
    @Id
    private String id;
    @ApiModelProperty("属性名")
    private String name;
    @ApiModelProperty("编码")
    private String code;
    @ApiModelProperty("值类型")
    private AttributeType type;

}

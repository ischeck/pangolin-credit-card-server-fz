package cn.fintecher.pangolin.entity.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

@Data
@Document(indexName = "collection_case_history", type = "collection_case_history", shards = 1, replicas = 0)
@ApiModel(value = "collectionCaseHistory", description = "案件变更历史记录")
public class CollectionCaseHistory extends BaseCase {

    @ApiModelProperty(notes = "操作员")
    private String operatorId;

    @ApiModelProperty(notes = "操作时间")
    private Date operatorTimeHistory;

}

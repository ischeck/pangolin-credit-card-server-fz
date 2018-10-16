package cn.fintecher.pangolin.service.domain.model.request;

import cn.fintecher.pangolin.common.utils.ZWStringUtils;
import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.List;
import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

/**
 * @Author : huaynmin
 * @Description : 案件删除
 * @Date : 2018/9/19+.
 */
@Data
@ApiModel(value = "StopCollectionCaseModel", description = "案件删除")
public class DeleteCollectionCaseModel extends SearchRequest {

    @ApiModelProperty("案件的ID")
    private List<String> ids;

    @ApiModelProperty("批次号")
    private String batchNumber;

    @Override
    public QueryBuilder generateQueryBuilder() {
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        if(Objects.nonNull(this.ids)) {
            qb.must(termsQuery("id.keyword", ids));
        }
        if(ZWStringUtils.isNotEmpty(this.batchNumber)) {
            qb.must(matchPhraseQuery("batchNumber", this.batchNumber));
        }
        return qb;
    }
}

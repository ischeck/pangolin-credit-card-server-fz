package cn.fintecher.pangolin.service.domain.model.request;

import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.List;
import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

/**
 * Created by BBG on 2018/8/10.
 */
@Data
public class IssuedCaseRequest extends SearchRequest {

    @ApiModelProperty("批次号")
    private String batchNumber;

    @ApiModelProperty("案件ID")
    private List<String> caseIds;


    @Override
    public BoolQueryBuilder generateQueryBuilder() {
        BoolQueryBuilder qb = QueryBuilders
                .boolQuery();
        if(!StringUtils.isBlank(batchNumber)){
            qb.must(matchPhraseQuery("batchNumber", this.batchNumber));
        }
        if (Objects.nonNull(this.caseIds)) {
            qb.must(termsQuery("id.keyword", this.caseIds));
        }
        return qb;
    }
}

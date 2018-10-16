package cn.fintecher.pangolin.service.domain.model.request;

import cn.fintecher.pangolin.common.enums.CaseIssuedFlag;
import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 9:02 2018/9/12
 */
@Data
public class CaseCommonProRequest extends SearchRequest {

    @ApiModelProperty("批次号")
    private String batchNumber;

    @ApiModelProperty("案件状态")
    private CaseIssuedFlag issuedFlag;


    @Override
    public BoolQueryBuilder generateQueryBuilder() {
        BoolQueryBuilder qb = QueryBuilders .boolQuery();
        if(!StringUtils.isBlank(batchNumber)){
            qb.must(matchPhraseQuery("batchNumber", batchNumber));
        }
        if(Objects.nonNull(issuedFlag)){
            qb.must(matchPhraseQuery("issuedFlag",issuedFlag.name()));
        }
        return qb;
    }
}

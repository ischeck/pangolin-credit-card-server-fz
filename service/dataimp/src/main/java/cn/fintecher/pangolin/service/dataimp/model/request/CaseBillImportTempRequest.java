package cn.fintecher.pangolin.service.dataimp.model.request;

import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

/**
 * @Author:peishouwen
 * @Desc: 案件对账导入
 * @Date:Create in 23:51 2018/8/1
 */
@Data
public class CaseBillImportTempRequest extends SearchRequest {

    @ApiModelProperty(notes = "批次号")
    private String batchNumber;

    @ApiModelProperty(notes = "案件号")
    private String caseNumber;

    @ApiModelProperty(notes = "帐号")
    private String account;

    @ApiModelProperty(notes = "操作批次号")
    private String operBatchNumber;

    @Override
    public QueryBuilder generateQueryBuilder() {
        BoolQueryBuilder queryBuilder= QueryBuilders.boolQuery();
        if(!StringUtils.isBlank(batchNumber)){
            queryBuilder.must(matchPhraseQuery("batchNumber",batchNumber));
        }
        if(!StringUtils.isBlank(caseNumber)){
            queryBuilder.must(matchPhraseQuery("caseNumber",caseNumber));
        }
        if(!StringUtils.isBlank(account)){
            queryBuilder.must((matchPhraseQuery("account",account)));
        }

        if(!StringUtils.isBlank(operBatchNumber)){
            queryBuilder.must(matchPhraseQuery("operBatchNumber",operBatchNumber));
        }
        return queryBuilder;
    }

}

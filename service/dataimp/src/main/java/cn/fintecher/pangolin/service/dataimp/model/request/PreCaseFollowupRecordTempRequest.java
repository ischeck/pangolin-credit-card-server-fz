package cn.fintecher.pangolin.service.dataimp.model.request;

import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 18:44 2018/8/3
 */
public class PreCaseFollowupRecordTempRequest extends SearchRequest {
    @ApiModelProperty(notes = "案件号")
    private String caseNumber;

    @ApiModelProperty(notes = "证件号")
    private String certificateNo;

    @ApiModelProperty(notes = "帐号")
    private String account;

    @Override
    public QueryBuilder generateQueryBuilder() {
        BoolQueryBuilder queryBuilder= QueryBuilders.boolQuery();
        if(!StringUtils.isBlank(certificateNo)){
            queryBuilder.must(matchPhraseQuery("certificateNo",certificateNo));
        }
        if(StringUtils.isBlank(caseNumber)){
            queryBuilder.must(matchPhraseQuery("caseNumber",caseNumber));
        }
        if(StringUtils.isBlank(account)){
            queryBuilder.must((matchPhraseQuery("account",account)));
        }
        return queryBuilder;
    }

}

package cn.fintecher.pangolin.service.dataimp.model.request;

import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import static org.elasticsearch.index.query.QueryBuilders.fuzzyQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 9:38 2018/7/31
 */
@Data
public class BaseCaseAllImportExcelTempRequest extends SearchRequest {

    @ApiModelProperty(notes = "批次号")
    private String batchNumber;

    @ApiModelProperty(notes = "客户姓名")
    private String personalName;

    @ApiModelProperty(notes = "证件号")
    private String certificateNo;

    @Override
    public QueryBuilder generateQueryBuilder() {
        BoolQueryBuilder queryBuilder= QueryBuilders.boolQuery();
        if(!StringUtils.isBlank(personalName)){
            queryBuilder.must(matchPhraseQuery("personalName",personalName));
        }

        if(!StringUtils.isBlank(certificateNo)){
            queryBuilder.must(matchPhraseQuery("certificateNo",certificateNo));
        }

        if(!StringUtils.isBlank(batchNumber)){
            queryBuilder.must(matchPhraseQuery("batchNumber",batchNumber));
        }
        return queryBuilder;
    }
}

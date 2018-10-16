package cn.fintecher.pangolin.service.domain.model.request;

import cn.fintecher.pangolin.common.enums.CaseIssuedFlag;
import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 13:51 2018/9/12
 */
@Data
public class CaseDistributeRequest extends SearchRequest{
    @ApiModelProperty("批次号")
    private String batchNumber;

    @ApiModelProperty("案件状态")
    private CaseIssuedFlag issuedFlag;

    @ApiModelProperty("城市数据")
    private Set<String> citys=new HashSet<>();

    @ApiModelProperty("催收员")
    private Set<String> collectors=new HashSet<>();

    @ApiModelProperty("案件所属部门")
    private Set<String> departs=new HashSet<>();

    @Override
    public BoolQueryBuilder generateQueryBuilder() {
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        if(!StringUtils.isBlank(batchNumber)){
            qb.must(matchPhraseQuery("batchNumber", batchNumber));
        }
        if(Objects.nonNull(issuedFlag)){
            qb.must(matchPhraseQuery("issuedFlag",issuedFlag.name()));
        }
        if(!citys.isEmpty()){
            qb.must(termsQuery("city.keyword",citys));
        }
        if(!collectors.isEmpty()){
            qb.must(termsQuery("currentCollector.fullName.keyword",collectors));
        }
        if(!departs.isEmpty()){
            qb.must(termsQuery("detaptName.keyword",departs));
        }
        return qb;
    }
}

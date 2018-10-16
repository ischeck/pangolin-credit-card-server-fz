package cn.fintecher.pangolin.service.dataimp.model.request;

import cn.fintecher.pangolin.common.enums.CaseIssuedFlag;
import cn.fintecher.pangolin.common.enums.DistributeWay;
import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

/**
 * @Author:peishouwen
 * @Desc: 区域案件分配
 * @Date:Create in 14:42 2018/8/9
 */
@Data
public class AreaCaseDistributeBatchRequest extends SearchRequest {


    @ApiModelProperty(notes = "案件总数")
    private Long caseNumTotal=new Long(0);

    @ApiModelProperty(notes = "案件总金额")
    private Double caseAmtTotal=new Double(0);

    @ApiModelProperty(notes = "区域总数")
    private Long collectorTotal=new Long(0);

    @ApiModelProperty(notes = "分配规则")
    private List<DistributeConfigModel> distributeConfigModels;

    @ApiModelProperty(notes = "分配方式")
    private DistributeWay distributeWay;

    @ApiModelProperty("批次号")
    private String batchNumber;

    @ApiModelProperty("案件状态")
    private CaseIssuedFlag issuedFlag;

    @ApiModelProperty("城市数据")
    private Set<String> citys=new HashSet<>();


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
        return qb;
    }
}

package cn.fintecher.pangolin.service.domain.model.request;

import cn.fintecher.pangolin.common.enums.CaseDataStatus;
import cn.fintecher.pangolin.common.enums.CaseIssuedFlag;
import cn.fintecher.pangolin.common.enums.ImportDataExcelStatus;
import cn.fintecher.pangolin.common.utils.ZWStringUtils;
import cn.fintecher.pangolin.common.web.SearchRequest;
import cn.fintecher.pangolin.entity.domain.ImportDataExcelRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.existsQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

/**
 * Created by BBG on 2018/8/7.
 */
@Data
@ApiModel("分案统计查询参数")
public class DistributeCaseSearchRequest extends SearchRequest {

    @ApiModelProperty(notes = "委托方ID")
    private String principalId;

    @ApiModelProperty(notes = "批次号")
    private String batchNumber;

    @ApiModelProperty(notes = "委案日期开始")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date delegationDateStart;

    @ApiModelProperty(notes = "委案日期结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date delegationDateEnd;

    @ApiModelProperty(notes = "退案日期开始")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endCaseDateStart;

    @ApiModelProperty(notes = "退案日期结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endCaseDateEnd;



    @ApiModelProperty(notes = "案件分池")
    private CaseIssuedFlag casePool;

    @Override
    public QueryBuilder generateQueryBuilder() {
        BoolQueryBuilder qb = QueryBuilders
                .boolQuery();

        qb.must(matchPhraseQuery("importDataExcelStatus", ImportDataExcelStatus.CONFIRMED.name()));

        if (ZWStringUtils.isNotEmpty(this.principalId)) {
            qb.must(matchPhraseQuery("principal.id", this.principalId));
        }
        if (ZWStringUtils.isNotEmpty(this.batchNumber)) {
            qb.must(matchPhraseQuery("batchNumber", this.batchNumber));
        }
        if (Objects.nonNull(this.delegationDateStart)) {
            qb.must(rangeQuery("delegationDate").gte(this.delegationDateStart.getTime()));
        }
        if (Objects.nonNull(this.delegationDateEnd)) {
            qb.must(rangeQuery("delegationDate").lte(this.delegationDateEnd.getTime() + 86400000));
        }
        if (Objects.nonNull(this.endCaseDateEnd)) {
            qb.must(rangeQuery("endCaseDate").gte(this.endCaseDateEnd.getTime()));
        }
        if (Objects.nonNull(this.endCaseDateEnd)) {
            qb.must(rangeQuery("endCaseDate").lte(this.endCaseDateEnd.getTime() + 86400000));
        }
        return qb;
    }
}

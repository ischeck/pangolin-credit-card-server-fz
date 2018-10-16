package cn.fintecher.pangolin.service.dataimp.model.request;

import cn.fintecher.pangolin.common.enums.CaseDataStatus;
import cn.fintecher.pangolin.common.utils.ZWStringUtils;
import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

@Data
public class ExportSearchRequest extends SearchRequest {

    @ApiModelProperty("模板ID")
    private String configId;

    @ApiModelProperty("委托方")
    private String principalId;

    @ApiModelProperty("批次号")
    private String batchNumber;

    @ApiModelProperty("案件ID集合")
    private List<String> caseIds;

    @ApiModelProperty("催收员")
    private String currentCollectorName;

    @ApiModelProperty("客户姓名")
    private String personalName;

    @ApiModelProperty("数据状态")
    private CaseDataStatus caseDataStatus;

    @ApiModelProperty("身份证号码")
    private String certificateNo;

    @ApiModelProperty("案件号")
    private String caseNumber;

    @ApiModelProperty("城市")
    private String city;

    @ApiModelProperty("委案日期开始")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date delegationDateStart;

    @ApiModelProperty("委案日期结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date delegationDateEnd;

    @ApiModelProperty("退案日期开始")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endCaseDateStart;

    @ApiModelProperty("退案日期结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endCaseDateEnd;

    @ApiModelProperty("手数")
    private String handsNumber;

    @ApiModelProperty("委托方名称")
    private String principalName;

    @Override
    public BoolQueryBuilder generateQueryBuilder() {
        BoolQueryBuilder qb = QueryBuilders
                .boolQuery();
        if (ZWStringUtils.isNotEmpty(this.principalId)) {
            qb.must(matchPhraseQuery("principal.id", this.principalId));
        }
        if (ZWStringUtils.isNotEmpty(this.principalName)) {
            qb.must(matchPhraseQuery("principal.name", this.principalName));
        }
        if (ZWStringUtils.isNotEmpty(this.batchNumber)) {
            qb.must(matchPhraseQuery("batchNumber", this.batchNumber));
        }
        if (Objects.nonNull(this.caseIds)) {
            qb.must(termsQuery("id.keyword", this.caseIds));
        }
        if (ZWStringUtils.isNotEmpty(this.personalName)) {
            qb.must(matchPhraseQuery("personal.personalName", this.personalName));
        }
        if (ZWStringUtils.isNotEmpty(this.delegationDateStart)) {
            qb.must(rangeQuery("delegationDate").gte(this.delegationDateStart.getTime()));
        }
        if (ZWStringUtils.isNotEmpty(this.delegationDateEnd)) {
            qb.must(rangeQuery("delegationDate").lte(this.delegationDateEnd.getTime()+86400000));
        }
        if (ZWStringUtils.isNotEmpty(this.endCaseDateStart)) {
            qb.must(rangeQuery("endCaseDate").gte(this.endCaseDateStart.getTime()));
        }
        if (ZWStringUtils.isNotEmpty(this.endCaseDateEnd)) {
            qb.must(rangeQuery("endCaseDate").lte(this.endCaseDateEnd.getTime()+86400000));
        }
        if (ZWStringUtils.isNotEmpty(this.handsNumber)) {
            qb.must(matchPhraseQuery("handsNumber", this.handsNumber));
        }
        if (ZWStringUtils.isNotEmpty(this.city)) {
            qb.must(matchPhraseQuery("city", this.city));
        }
        if (ZWStringUtils.isNotEmpty(this.caseNumber)) {
            qb.must(matchPhraseQuery("caseNumber", this.caseNumber));
        }
        if (ZWStringUtils.isNotEmpty(this.certificateNo)) {
            qb.must(matchQuery("personal.certificateNo", this.certificateNo));
        }
        if (ZWStringUtils.isNotEmpty(this.caseDataStatus)) {
            qb.must(matchPhraseQuery("caseDataStatus", this.caseDataStatus.name()));
        }
        if (ZWStringUtils.isNotEmpty(this.currentCollectorName)) {
            qb.must(matchPhraseQuery("currentCollector.name", this.currentCollectorName));
        }
        return qb;
    }

}

package cn.fintecher.pangolin.service.domain.model.request;

import cn.fintecher.pangolin.common.enums.CaseDataStatus;
import cn.fintecher.pangolin.common.enums.CaseLeaveFlag;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

/**
 * Created by BBG on 2018/8/10.
 */
@Data
public class CaseStateQueryRequest extends SearchRequest {

    @ApiModelProperty("客户姓名")
    private String personalName;

    @ApiModelProperty("账号")
    private String account;

    @ApiModelProperty("数据状态")
    private CaseDataStatus caseDataStatus;

    @ApiModelProperty("身份证号码")
    private String certificateNo;

    @ApiModelProperty("批次号")
    private String batchNumber;

    @ApiModelProperty("案件号")
    private String caseNumber;

    @ApiModelProperty("案件金额(￥)开始")
    private Double leftAmtStart;

    @ApiModelProperty("案件金额(￥)结束")
    private Double leftAmtEnd;

    @ApiModelProperty("案件金额($)开始")
    private Double leftAmtDollarStart;

    @ApiModelProperty("案件金额($)结束")
    private Double leftAmtDollarEnd;

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

    @ApiModelProperty("删除日期开始")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date deleteCaseDateStart;

    @ApiModelProperty("删除日期结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date deleteCaseDateEnd;

    @ApiModelProperty("停催时间开始")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date stopTimeStart;

    @ApiModelProperty("停催时间结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date stopTimeEnd;

    @ApiModelProperty("手数")
    private String handsNumber;

    @ApiModelProperty("委托方id")
    private String principal;

    @ApiModelProperty("委托方名称")
    private String principalName;

    @ApiModelProperty("催收员")
    private String currentCollectorName;

    @ApiModelProperty("留案标识")
    private CaseLeaveFlag leaveFlag;


    @Override
    public BoolQueryBuilder generateQueryBuilder() {
        BoolQueryBuilder qb = QueryBuilders
                .boolQuery();
        if (Objects.nonNull(this.personalName)) {
            qb.must(matchPhraseQuery("personal.personalName", this.personalName));
        }
        if (Objects.nonNull(this.account)) {
            qb.must(matchPhraseQuery("account", this.account));
        }
        if (Objects.nonNull(this.currentCollectorName)) {
            qb.must(matchPhraseQuery("currentCollector.fullName", this.currentCollectorName));
        }
        if (Objects.nonNull(this.delegationDateStart)) {
            qb.must(rangeQuery("delegationDate").gte(this.delegationDateStart.getTime()));
        }
        if (Objects.nonNull(this.delegationDateEnd)) {
            qb.must(rangeQuery("delegationDate").lte(this.delegationDateEnd.getTime()+86400000));
        }
        if (Objects.nonNull(this.endCaseDateStart)) {
            qb.must(rangeQuery("endCaseDate").gte(this.endCaseDateStart.getTime()));
        }
        if (Objects.nonNull(this.endCaseDateEnd)) {
            qb.must(rangeQuery("endCaseDate").lte(this.endCaseDateEnd.getTime()+86400000));
        }
        if (Objects.nonNull(this.deleteCaseDateStart)) {
            qb.must(rangeQuery("deleteCaseDateEnd").gte(this.deleteCaseDateStart.getTime()));
        }
        if (Objects.nonNull(this.deleteCaseDateEnd)) {
            qb.must(rangeQuery("deleteCaseDateEnd").lte(this.deleteCaseDateEnd.getTime()+86400000));
        }
        if (Objects.nonNull(this.stopTimeStart)) {
            qb.must(rangeQuery("stopTime").gte(this.stopTimeStart.getTime()));
        }
        if (Objects.nonNull(this.stopTimeEnd)) {
            qb.must(rangeQuery("stopTime").lte(this.stopTimeEnd.getTime()+86400000));
        }
        if (Objects.nonNull(this.principalName)) {
            qb.must(matchPhraseQuery("principal.principalName", this.principalName));
        }
        if (Objects.nonNull(this.principal)) {
            qb.must(matchPhraseQuery("principal.id", this.principal));
        }
        if (Objects.nonNull(this.handsNumber)) {
            qb.must(matchPhraseQuery("handsNumber", this.handsNumber));
        }
        if (Objects.nonNull(this.city)) {
            qb.must(matchPhraseQuery("city", this.city));
        }
        if (Objects.nonNull(this.leftAmtStart)) {
            qb.must(rangeQuery("leftAmtStart").gt(this.leftAmtStart));
        }
        if (Objects.nonNull(this.leftAmtEnd)) {
            qb.must(rangeQuery("leftAmtEnd").lt(this.leftAmtEnd));
        }
        if (Objects.nonNull(this.leftAmtDollarStart)) {
            qb.must(rangeQuery("leftAmtDollarStart").gt(this.leftAmtDollarStart));
        }
        if (Objects.nonNull(this.leftAmtDollarEnd)) {
            qb.must(rangeQuery("leftAmtDollarEnd").lt(this.leftAmtDollarEnd));
        }
        if (Objects.nonNull(this.batchNumber)) {
            qb.must(matchPhraseQuery("batchNumber", this.batchNumber));
        }
        if (Objects.nonNull(this.caseNumber)) {
            qb.must(matchPhraseQuery("caseNumber", this.caseNumber));
        }
        if (Objects.nonNull(this.certificateNo)) {
            qb.must(matchQuery("personal.certificateNo", this.certificateNo));
        }
        if (Objects.nonNull(this.caseDataStatus)) {
            qb.must(matchPhraseQuery("caseDataStatus", this.caseDataStatus.name()));
        }
        if (Objects.nonNull(this.leaveFlag)) {
            qb.must(matchPhraseQuery("leaveFlag", this.leaveFlag.name()));
        }
        return qb;
    }
}

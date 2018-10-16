package cn.fintecher.pangolin.service.domain.model.request;

import cn.fintecher.pangolin.common.enums.CaseFiller;
import cn.fintecher.pangolin.common.enums.CaseLeaveFlag;
import cn.fintecher.pangolin.common.enums.GroupType;
import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.*;

@Data
public class CollectionSearchRequest extends SearchRequest {

    @ApiModelProperty(notes = "委托方Id")
    private String principalId;

    @ApiModelProperty(notes = "批次号")
    private String batchNumber;

    @ApiModelProperty(notes = "姓名")
    private String name;

    @ApiModelProperty(notes = "催收员")
    private String currentCollector;

    @ApiModelProperty(notes = "组织id")
    private String organizationId;

    @ApiModelProperty(notes = "证件号码")
    private String certificateNo;

    @ApiModelProperty(notes = "手工状态" )
    private String collectionStatus;

    @ApiModelProperty(notes = "留案标识")
    private CaseLeaveFlag caseLeaveFlag;

    @ApiModelProperty(notes = "待催收/催收中标识")
    private Integer collectionRecordCount;

    @ApiModelProperty(notes = "案件金额(￥)开始")
    private Double leftAmtStart;

    @ApiModelProperty(notes = "案件金额(￥)结束")
    private Double leftAmtEnd;

    @ApiModelProperty(notes = "案件金额($)开始")
    private Double leftAmtDollarStart;

    @ApiModelProperty(notes = "案件金额($)结束")
    private Double leftAmtDollarEnd;

    @ApiModelProperty(notes = "城市")
    private String city;

    @ApiModelProperty(notes = "手数")
    private String handsNumber;

    @ApiModelProperty(notes = "小组案件标识")
    private GroupType groupType;

    @ApiModelProperty(notes = "小组案件标识")
    private CaseFiller caseFiller;

    @ApiModelProperty(notes = "案件编号")
    private String caseNumber;

    @ApiModelProperty(notes = "帐号")
    private String account;

    @Override
    public BoolQueryBuilder generateQueryBuilder() {
        BoolQueryBuilder qb = QueryBuilders
                .boolQuery();
        if (Objects.nonNull(this.name)) {
            qb.must(matchPhraseQuery("personal.personalName", this.name));
        }

        if (Objects.nonNull(this.certificateNo)) {
            qb.must(matchPhraseQuery("personal.certificateNo", this.certificateNo));
        }

        if (Objects.nonNull(this.currentCollector)) {
            qb.must(matchPhraseQuery("currentCollector.fullName", this.currentCollector));
        }

        if (Objects.nonNull(this.batchNumber)) {
            qb.must(matchPhraseQuery("batchNumber", this.batchNumber));
        }

        if (Objects.nonNull(this.caseNumber)) {
            qb.must(matchPhraseQuery("caseNumber", this.caseNumber));
        }

        if (Objects.nonNull(this.account)) {
            qb.must(matchPhraseQuery("account", this.account));
        }

        if (Objects.nonNull(this.principalId)) {
            qb.must(matchPhraseQuery("principal.id", this.principalId));
        }

        if(Objects.nonNull(this.organizationId)){
            qb.must(termQuery("departments", this.organizationId));
        }

        if (Objects.nonNull(this.handsNumber)) {
            qb.must(matchPhraseQuery("handsNumber", this.handsNumber));
        }

        if (Objects.nonNull(this.city)) {
            qb.must(matchPhraseQuery("city", this.city));
        }

        if(Objects.nonNull(this.leftAmtStart)) {
            qb.must(rangeQuery("leftAmt").gt(this.leftAmtStart));
        }

        if(Objects.nonNull(this.leftAmtEnd)){
            qb.must(rangeQuery("leftAmt").lt(this.leftAmtEnd));
        }

        if(Objects.nonNull(this.leftAmtDollarStart)) {
            qb.must(rangeQuery("leftAmtDollar").gt(this.leftAmtDollarStart));
        }

        if(Objects.nonNull(this.leftAmtDollarEnd)){
            qb.must(rangeQuery("leftAmtDollar").lt(this.leftAmtDollarEnd));
        }

        if(Objects.nonNull(this.collectionStatus)){
            qb.must(matchQuery("collectionStatus",this.collectionStatus));
        }

        if(Objects.nonNull(this.collectionRecordCount)){
            Integer count = 0;
            if(Objects.equals(this.collectionRecordCount, 0)){
                qb.must(rangeQuery("collectionRecordCount").lte(count));
            }else {
                qb.must(rangeQuery("collectionRecordCount").gt(count));
            }
        }
        return qb;
    }
}

package cn.fintecher.pangolin.service.domain.model.request;

import cn.fintecher.pangolin.common.enums.AssistFlag;
import cn.fintecher.pangolin.common.enums.AssistStatus;
import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.List;
import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

/**
 * @Author : huyanmin
 * @Description : 案件协催案件请求model
 * @Date : 2018/7/16.
 */
@Data
public class AssistCaseSearchRequest extends SearchRequest {

    @ApiModelProperty("外访案件Id")
    private List<String> assistCaseIds;

    @ApiModelProperty("姓名")
    private String personalName;

    @ApiModelProperty("委托方Id")
    private String principalId;

    @ApiModelProperty("委托方名称")
    private String principalName;

    @ApiModelProperty("证件号")
    private String idCard;

    @ApiModelProperty(notes = "案件编号")
    private String caseNumber;

    @ApiModelProperty(notes = "账号")
    private String account;

    @ApiModelProperty("批次号")
    private String batchNumber;

    @ApiModelProperty(notes = "协助状态")
    private AssistStatus assistStatus;

    @ApiModelProperty(notes = "协助标识-催收管理查询")
    private AssistFlag assistFlag;

    @ApiModelProperty(notes = "协助标识-协催管理查询")
    private AssistFlag assistLocalFlag;

    @ApiModelProperty(notes = "协助搜索标识")
    private AssistFlag assistSearchFlag;

    @ApiModelProperty(notes = "页数")
    private Integer page;

    @ApiModelProperty(notes = "每页大小")
    private Integer size;

    @ApiModelProperty(notes = "当前外放协助催收员")
    private String currentCollector;

    @Override
    public BoolQueryBuilder generateQueryBuilder() {
        BoolQueryBuilder qb = QueryBuilders
                .boolQuery();
        if (Objects.nonNull(this.assistCaseIds)) {
            qb.must(matchQuery("caseId", this.assistCaseIds));
        }

        if(Objects.nonNull(this.principalId)){
            qb.must(matchPhraseQuery("principal.id", this.principalId));
        }

        if(Objects.nonNull(this.principalName)){
            qb.must(matchPhraseQuery("principal.principalName", this.principalName));
        }

        if (Objects.nonNull(this.assistStatus)) {
            qb.must(matchPhraseQuery("assistStatus", this.assistStatus.toString()));
        }

        if (Objects.nonNull(this.batchNumber)) {
            qb.must(matchPhraseQuery("batchNumber", this.batchNumber));
        }

        if (Objects.nonNull(this.idCard)) {
            qb.must(matchPhraseQuery("idCard", this.idCard));
        }

        if (Objects.nonNull(this.personalName)) {
            qb.must(matchPhraseQuery("personalName", this.personalName));
        }

        if(Objects.nonNull(this.assistSearchFlag)){
            qb.must(matchPhraseQuery("assistFlag", this.assistSearchFlag.toString()));
        }

        if(Objects.nonNull(this.currentCollector)){
            qb.must(matchPhraseQuery("currentCollector.fullName", this.currentCollector));
        }

        if (Objects.nonNull(this.account)) {
            qb.must(matchPhraseQuery("account", this.account));
        }

        if (Objects.nonNull(this.caseNumber)) {
            qb.must(matchPhraseQuery("caseNumber", this.caseNumber));
        }
        //协催管理查询
        if(Objects.nonNull(this.assistLocalFlag)){
            if(this.assistLocalFlag.equals(AssistFlag.LOCAL_OUT_ASSIST)){
                qb.must(matchPhraseQuery("assistFlag", this.assistLocalFlag.toString()));
            }else {
                qb.must(QueryBuilders.boolQuery().should(matchPhraseQuery("assistFlag", AssistFlag.OFFSITE_PHONE_ASSIST.toString()))
                .should(matchPhraseQuery("assistFlag", AssistFlag.OFFSITE_OUT_ASSIST.toString())));
            }
        }

        if (Objects.nonNull(this.assistFlag)) {
            if(this.assistFlag.equals(AssistFlag.OFFSITE_PHONE_ASSIST)){
               qb.must(matchPhraseQuery("assistFlag", this.assistFlag.toString()));
            }else {
                qb.must(QueryBuilders.boolQuery().should(matchPhraseQuery("assistFlag", AssistFlag.LOCAL_OUT_ASSIST.toString()))
                        .should(matchPhraseQuery("assistFlag", AssistFlag.OFFSITE_OUT_ASSIST.toString())));
            }
        }
        return qb;
    }

}

package cn.fintecher.pangolin.service.domain.model.request;

import cn.fintecher.pangolin.common.enums.AssistFlag;
import cn.fintecher.pangolin.common.enums.AssistStatus;
import cn.fintecher.pangolin.common.utils.ZWStringUtils;
import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * @Author : BBG
 * @Description : APP案件查询
 * @Date : 2018/8/28.
 */
@Data
public class AppCaseSearchRequest extends SearchRequest {


    @ApiModelProperty("姓名")
    private String personalName;

    @ApiModelProperty("地址")
    private String addressDetail;

    @ApiModelProperty(notes = "待催收 0/催收中 1标识")
    private Integer collectionRecordCount;

    @Override
    public BoolQueryBuilder generateQueryBuilder() {
        BoolQueryBuilder qb = QueryBuilders
                .boolQuery();

        if (ZWStringUtils.isNotEmpty(this.personalName)) {
            qb.must(matchPhraseQuery("personalName", this.personalName));
        }
        if (ZWStringUtils.isNotEmpty(this.addressDetail)) {
            qb.must(matchQuery("addressDetail", this.addressDetail));
        }
        qb.must(QueryBuilders.boolQuery().should(matchPhraseQuery("assistFlag", AssistFlag.LOCAL_OUT_ASSIST.toString()))
                .should(matchPhraseQuery("assistFlag", AssistFlag.OFFSITE_OUT_ASSIST.toString())));
        qb.must(matchPhraseQuery("assistStatus", AssistStatus.ASSIST_COLLECTING.name()));

        return qb;
    }

}

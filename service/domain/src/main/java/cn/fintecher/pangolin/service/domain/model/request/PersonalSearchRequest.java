package cn.fintecher.pangolin.service.domain.model.request;

import cn.fintecher.pangolin.common.enums.Sex;
import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

@Data
public class PersonalSearchRequest extends SearchRequest {
    @ApiModelProperty(notes = "姓名")
    private String name;

    @ApiModelProperty(notes = "性别")
    private Sex sex;

    @ApiModelProperty(notes = "身份证号码")
    private String idCard;

    @ApiModelProperty(notes = "生日开始时间段")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;

    @ApiModelProperty(notes = "生日结束时间段")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;

    @Override
    public QueryBuilder generateQueryBuilder() {
        BoolQueryBuilder qb = QueryBuilders
                .boolQuery();
        if (Objects.nonNull(this.name)) {
            qb.must(matchPhraseQuery("name", this.name));
        }
        if (Objects.nonNull(this.sex)) {
            qb.must(matchPhraseQuery("sex", this.sex.name()));
        }
        if (Objects.nonNull(this.idCard)) {
            qb.must(matchPhraseQuery("idCard", this.idCard));
        }
        if (Objects.nonNull(this.startTime) && Objects.nonNull(this.endTime)) {
            qb.must(rangeQuery("birthday").gte(this.startTime.getTime()).lte(this.endTime.getTime()));
        }
        return qb;
    }
}

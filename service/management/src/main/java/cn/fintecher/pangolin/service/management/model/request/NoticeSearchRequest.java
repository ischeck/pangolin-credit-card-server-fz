package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.enums.NoticeType;
import cn.fintecher.pangolin.common.web.MongoSearchRequest;
import cn.fintecher.pangolin.entity.managentment.QNotice;
import com.querydsl.core.BooleanBuilder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

@Data
public class NoticeSearchRequest extends MongoSearchRequest {

    @ApiModelProperty(notes = "类型")
    private NoticeType type;

    @ApiModelProperty("发布人")
    private String operatorName;

    @ApiModelProperty(notes = "关键字")
    private String keyWords;


    @Override
    public BooleanBuilder generateQueryBuilder() {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QNotice qNotice = QNotice.notice;
        if (Objects.nonNull(this.type)) {
            booleanBuilder.and(qNotice.type.eq(this.type));
        }
        if (Objects.nonNull(this.operatorName)) {
            booleanBuilder.and(qNotice.operatorName.contains(this.operatorName));
        }
        if (Objects.nonNull(this.keyWords)) {
            booleanBuilder.and(qNotice.title.contains(this.keyWords).or(qNotice.content.contains(this.keyWords)));
        }

        return booleanBuilder;
    }


}

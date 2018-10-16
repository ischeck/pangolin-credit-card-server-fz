package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.enums.SensitiveLevel;
import cn.fintecher.pangolin.common.utils.ZWStringUtils;
import cn.fintecher.pangolin.common.web.MongoSearchRequest;
import cn.fintecher.pangolin.entity.managentment.QSensitiveWord;
import com.querydsl.core.BooleanBuilder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

@Data
public class SensitiveWordSearchRequest extends MongoSearchRequest {

    @ApiModelProperty("委托方")
    private String principalId;

    @ApiModelProperty("级别")
    private SensitiveLevel level;

    @ApiModelProperty("敏感词")
    private String word;

    @Override
    public BooleanBuilder generateQueryBuilder() {
        BooleanBuilder builder = new BooleanBuilder();
        if(ZWStringUtils.isNotEmpty(this.principalId)){
            builder.and(QSensitiveWord.sensitiveWord.principalId.eq(this.principalId));
        }
        if(Objects.nonNull(this.level)){
            builder.and(QSensitiveWord.sensitiveWord.level.eq(this.level));
        }
        if(ZWStringUtils.isNotEmpty(this.word)){
            builder.and(QSensitiveWord.sensitiveWord.word.contains(this.word));
        }
        return builder;
    }
}

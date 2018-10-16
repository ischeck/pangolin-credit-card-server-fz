package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.utils.ZWStringUtils;
import cn.fintecher.pangolin.common.web.MongoSearchRequest;
import cn.fintecher.pangolin.entity.managentment.QPeriodTransformTemplate;
import com.querydsl.core.BooleanBuilder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PeriodTransformSearchRequest extends MongoSearchRequest {

    @ApiModelProperty("委托方")
    private String principalId;

    @Override
    public BooleanBuilder generateQueryBuilder() {
        BooleanBuilder builder = new BooleanBuilder();
        if(ZWStringUtils.isNotEmpty(this.principalId)){
            builder.and(QPeriodTransformTemplate.periodTransformTemplate.principalId.eq(this.principalId));
        }
        return builder;
    }
}

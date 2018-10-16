package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.enums.CustConfigType;
import cn.fintecher.pangolin.common.utils.ZWStringUtils;
import cn.fintecher.pangolin.common.web.MongoSearchRequest;
import cn.fintecher.pangolin.entity.managentment.QCustConfig;
import com.querydsl.core.BooleanBuilder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

/**
 * Created by BBG on 2018/8/1.
 */
@Data
public class CustConfigSearchRequest extends MongoSearchRequest {
    @ApiModelProperty(notes = "类型")
    private CustConfigType custConfigType;

    @ApiModelProperty(notes = "委托方ID")
    private String principalId;



    @Override
    public BooleanBuilder generateQueryBuilder() {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QCustConfig qCustConfig = QCustConfig.custConfig;
        if (Objects.nonNull(this.custConfigType)) {
            booleanBuilder.and(qCustConfig.custConfigType.eq(this.custConfigType));
        }
        if(ZWStringUtils.isNotEmpty(this.principalId)){
            booleanBuilder.and(qCustConfig.principalId.eq(this.principalId));
        }
        return booleanBuilder;
    }
}


package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.web.MongoSearchRequest;
import cn.fintecher.pangolin.entity.managentment.QComplianceConfig;
import com.querydsl.core.BooleanBuilder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * Created by ChenChang on 2018/8/31.
 */
@Data
public class QueryComplianceConfigRequest extends MongoSearchRequest {
    @ApiModelProperty("组织名称")
    private String organizationName;

    @Override
    public BooleanBuilder generateQueryBuilder() {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QComplianceConfig qComplianceConfig = QComplianceConfig.complianceConfig;
        if (StringUtils.isNotBlank(organizationName)) {
            booleanBuilder.and(qComplianceConfig.organization.name.contains(organizationName));
        }
        return booleanBuilder;
    }
}

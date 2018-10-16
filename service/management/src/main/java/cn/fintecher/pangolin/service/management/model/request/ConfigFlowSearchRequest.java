package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.enums.ApplyType;
import cn.fintecher.pangolin.common.enums.PrincipalType;
import cn.fintecher.pangolin.common.web.MongoSearchRequest;
import cn.fintecher.pangolin.entity.managentment.QApproveFlowConfig;
import cn.fintecher.pangolin.entity.managentment.QPrincipal;
import com.querydsl.core.BooleanBuilder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

@Data
public class ConfigFlowSearchRequest extends MongoSearchRequest {

    @ApiModelProperty(notes = "配置名称")
    private String configName;

    @ApiModelProperty(notes = "申请类型")
    private ApplyType configType;


    @Override
    public BooleanBuilder generateQueryBuilder() {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QApproveFlowConfig qApproveFlowConfig = QApproveFlowConfig.approveFlowConfig;
        if (Objects.nonNull(this.configName)) {
            booleanBuilder.and(qApproveFlowConfig.configName.contains(this.configName));
        }

        if (Objects.nonNull(this.configType)) {
            booleanBuilder.and(qApproveFlowConfig.configType.eq(this.configType));
        }

        return booleanBuilder;
    }


}

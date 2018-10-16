package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.enums.ComplianceState;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by ChenChang on 2018/8/31.
 */
@Data
public class ModifyComplianceConfigRequest extends CreateComplianceConfigRequest {
    @NotNull(message = "{id.is.required}")
    private String id;

    private ComplianceState state;
}

package cn.fintecher.pangolin.service.domain.model.request;

import cn.fintecher.pangolin.common.enums.AssistFlag;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @description 撤回协催请求
 * Created by huyanmin on 2018/8/10.
 */
@Data
public class RetractAssistCaseRequest {
    @ApiModelProperty("地址记录ID")
    private String personalAddressId;

    @ApiModelProperty("记录ID")
    private String personalContactId;

    @ApiModelProperty("案件Id")
    private String caseId;

    @ApiModelProperty("协催类型")
    private AssistFlag assistContactFlag;

    @ApiModelProperty("协催标识")
    private AssistFlag assistAddressFlag;
}

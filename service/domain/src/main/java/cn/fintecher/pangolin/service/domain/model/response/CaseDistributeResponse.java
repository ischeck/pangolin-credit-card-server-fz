package cn.fintecher.pangolin.service.domain.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 13:59 2018/9/12
 */
@Data
public class CaseDistributeResponse {
    @ApiModelProperty("案件数量")
    private Long caseNumber=0L;
    @ApiModelProperty("案件金额")
    private Double caseAmt=0.0;
}

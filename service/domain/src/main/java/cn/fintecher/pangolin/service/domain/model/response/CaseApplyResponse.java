package cn.fintecher.pangolin.service.domain.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author : huyanmin
 * @Description : 公共案件申请
 * @Date : 2018/7/18.
 */
@Data
public class CaseApplyResponse extends BasicCaseApplyResponse {

    @ApiModelProperty("批次号")
    private String batchNumber;

    @ApiModelProperty("姓名")
    private String personalName;

    @ApiModelProperty("证件号")
    private String idCards;

}

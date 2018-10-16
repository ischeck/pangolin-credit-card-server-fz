package cn.fintecher.pangolin.service.domain.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by BBG on 2018/8/7.
 */
@Data
public class CaseSignModel {
    @ApiModelProperty("案件ID")
    private String caseId;

    @ApiModelProperty("催收状态")
    private String collState;
}

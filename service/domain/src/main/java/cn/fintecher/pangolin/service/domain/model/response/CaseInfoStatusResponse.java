package cn.fintecher.pangolin.service.domain.model.response;

import cn.fintecher.pangolin.entity.domain.Personal;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

/**
 * Created by huyanmin 2018/07/16
 */

@Data
public class CaseInfoStatusResponse {

    private String id;

    @ApiModelProperty(notes = "案件Id")
    private String caseId;

    @ApiModelProperty(notes = "客户信息")
    private Personal personal;

    @ApiModelProperty(notes = "催收状态")
    private Set<String> collectionStatus;

    @ApiModelProperty(notes = "余额")
    private Double leftAmt;
}

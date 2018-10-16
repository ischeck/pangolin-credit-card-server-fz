package cn.fintecher.pangolin.service.domain.model.request;

import cn.fintecher.pangolin.common.enums.CaseDataStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author : huaynmin
 * @Description : 案件停催
 * @Date : 2018/9/19+.
 */
@Data
@ApiModel(value = "StopCollectionCaseModel", description = "案件停催")
public class StopCollectionCaseModel {

    @ApiModelProperty("案件的ID")
    private List<String> idList;

    @ApiModelProperty("案件状态")
    private CaseDataStatus caseDataStatus;

}

package cn.fintecher.pangolin.service.domain.model;

import cn.fintecher.pangolin.service.domain.model.response.CollectionCaseByBatchNumber;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @Author : huyanmin
 * @Description : 案件查询结果
 * @Date : 2018/7/30.
 */
@Data
public class CollectionCaseModel {

    @ApiModelProperty("批次号")
    private Map<String, CollectionCaseByBatchNumber> colCaseMap;
}

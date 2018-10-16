package cn.fintecher.pangolin.entity.domain;

import cn.fintecher.pangolin.common.enums.ApplyFileContent;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;


/**
 * @Author : huyanmin
 * @Description : 资料申请
 * @Date : 2018/7/20.
 */
@Data
@Document(indexName = "material_case_apply", type = "material_case_apply", shards = 1, replicas = 0)
@ApiModel(value = "MaterialCaseApply", description = "资料申请")
public class MaterialCaseApply extends BasicCaseApply {

    @ApiModelProperty("申调类型")
    private ApplyFileContent applyFileContent;


}

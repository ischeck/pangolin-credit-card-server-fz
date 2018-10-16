package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.entity.managentment.ImportExcelConfigItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: huyanminn
 * @Description: 模板配置请求模板
 * @Date 2018/7/3
 */
@Data
@Document
@ApiModel(value = "ModifyTemplateDataInfoRequest",
        description = "模板配置修改模板")
public class ModifyTemplateDataInfoRequest implements Serializable {

    @ApiModelProperty("模板id")
    public String id;

    @ApiModelProperty("Excel模板关联配置")
    private List<ImportExcelConfigItem> templateExcelInfoList;
}

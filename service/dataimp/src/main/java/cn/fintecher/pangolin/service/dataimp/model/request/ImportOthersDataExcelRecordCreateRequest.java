package cn.fintecher.pangolin.service.dataimp.model.request;

import cn.fintecher.pangolin.common.enums.ImportDataExcelStatus;
import cn.fintecher.pangolin.common.enums.TemplateType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @Author:peishouwen
 * @Desc:其他案件信息导入记录
 * @Date:Create in 14:34 2018/7/28
 */
@Data
public class ImportOthersDataExcelRecordCreateRequest {


    @ApiModelProperty(notes = "外键:文件ID")
    private String fileId;

    @ApiModelProperty(notes = "委托方id标识")
    private String principalId;

    @ApiModelProperty(notes = "委托方名称")
    private String principalName;

    @ApiModelProperty("Excel 模板ID")
    private String templateId;

    @ApiModelProperty("模板类型")
    private TemplateType templateType;


}

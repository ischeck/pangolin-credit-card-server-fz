package cn.fintecher.pangolin.entity.domain;

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
@Document(indexName = "import_others_data_excel_record", type = "import_others_data_excel_record", shards = 1, replicas = 0)
@ApiModel(value = "ImportOthersDataExcelRecord", description = "其他案件信息导入记录")
public class ImportOthersDataExcelRecord {

    @ApiModelProperty("唯一标识（主键）")
    @Id
    private String id;

    @ApiModelProperty(notes = "外键:文件ID")
    private String fileId;

    @ApiModelProperty(notes = "操作批次号")
    private String operBatchNumber;

    @ApiModelProperty(notes = "委托方id标识")
    private String principalId;

    @ApiModelProperty(notes = "委托方名称")
    private String principalName;

    @ApiModelProperty("Excel 模板ID")
    private String templateId;

    @ApiModelProperty("模板类型")
    private TemplateType templateType;

    @ApiModelProperty("备注文件URL")
    private String resultUrl;

    @ApiModelProperty("数据导入状态")
    private ImportDataExcelStatus importDataExcelStatus;

    @ApiModelProperty("案件数量")
    private Long caseTotal;

    @ApiModelProperty("创建时间")
    @Field(type = FieldType.Date)
    private Date operatorTime;

    @ApiModelProperty("操作人姓名")
    private String operatorName;

    @ApiModelProperty("操作人")
    private String operatorUserName;

}

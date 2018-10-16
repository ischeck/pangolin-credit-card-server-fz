package cn.fintecher.pangolin.entity.domain;

import cn.fintecher.pangolin.common.annotation.ExcelAnno;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.Map;

/**
 * @Author:peishouwen
 * @Desc: 案件警告信息
 * @Date:Create in 0:18 2018/8/2
 */
@Data
@Document(indexName = "case_warn_import_temp", type = "case_warn_import_temp", shards = 1, replicas = 0)
@ApiModel(value = "CaseWarnImportTemp", description = "案件警告信息")
public class CaseWarnImportTemp {
    @Id
    private String id;

    @ApiModelProperty(notes = "MD5生成主键")
    private String primaryKey;

    @ApiModelProperty(notes = "批次号")
    @ExcelAnno(cellName = "批次号",fieldDataType = ExcelAnno.FieldDataType.STRING,fieldType = ExcelAnno.FieldType.CASE)
    private String batchNumber;

    @ApiModelProperty(notes = "案件编号")
    @ExcelAnno(cellName = "案件编号",fieldDataType = ExcelAnno.FieldDataType.STRING,fieldType = ExcelAnno.FieldType.CASE)
    private String caseNumber;

    @ApiModelProperty(notes = "帐号")
    @ExcelAnno(cellName = "帐号",fieldDataType = ExcelAnno.FieldDataType.STRING,fieldType = ExcelAnno.FieldType.CASE)
    private String account;

    @ApiModelProperty(notes = "卡号")
    @ExcelAnno(cellName = "卡号",fieldDataType = ExcelAnno.FieldDataType.STRING,fieldType = ExcelAnno.FieldType.CASE)
    private String cardNo;

    @ApiModelProperty(notes = "证件号")
    @ExcelAnno(cellName = "证件号",fieldDataType = ExcelAnno.FieldDataType.STRING,fieldType = ExcelAnno.FieldType.CASE)
    private String certificateNo;

    @ApiModelProperty(notes = "客户姓名")
    @ExcelAnno(cellName = "客户姓名",fieldDataType = ExcelAnno.FieldDataType.STRING,fieldType = ExcelAnno.FieldType.CASE)
    private String personalName;

    @ApiModelProperty(notes = "警告信息")
    @ExcelAnno(cellName = "警告信息",fieldDataType = ExcelAnno.FieldDataType.STRING,fieldType = ExcelAnno.FieldType.CASE)
    private String msg;

    @ApiModelProperty(notes = "操作批次号")
    private String operBatchNumber;

    @ApiModelProperty(notes = "委托方id标识")
    private String principalId;

    @ApiModelProperty(notes = "委托方名称")
    private String principalName;

    @ApiModelProperty(notes = "操作员")
    private String operator;

    @ApiModelProperty(notes = "操作时间")
    @Field(type = FieldType.Date)
    private Date operatorTime;

    @ApiModelProperty(notes = "导入时不匹配字段")
    private Map<String, String> remarkMap;
}

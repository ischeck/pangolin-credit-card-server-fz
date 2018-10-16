package cn.fintecher.pangolin.entity.domain;

import cn.fintecher.pangolin.common.annotation.ExcelAnno;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author : huyanmin
 * @Description : 委前催计
 * @Date : 15:49 2018/7/5
 */
@Data
@Document(indexName = "pre_case_followup_record_temp", type = "pre_case_followup_record_temp", shards = 1, replicas = 0)
@ApiModel(value = "preCaseFollowupRecordTemp", description = "委前催计")
public class PreCaseFollowupRecordTemp {

    @Id
    @ApiModelProperty(notes = "id")
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

    @ApiModelProperty(notes = "催记时间")
    @Field(type = FieldType.Date)
    private Date followTime;

    @ApiModelProperty(notes = "催记内容")
    @ExcelAnno(cellName = "催记内容",fieldDataType = ExcelAnno.FieldDataType.STRING,fieldType = ExcelAnno.FieldType.CASE)
    private String content;

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

}
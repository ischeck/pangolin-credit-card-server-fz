package cn.fintecher.pangolin.entity.domain;

import cn.fintecher.pangolin.common.annotation.ExcelAnno;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author:peishouwen
 * @Desc: 接收Excel导入数据临时数据表
 * @Date:Create in 17:28 2018/7/25
 */
@Data
@Document(indexName = "base_personal_import_excel_temp", type = "base_personal_import_excel_temp", shards = 1, replicas = 0)
@ApiModel(value = "BasePersonalImportExcelTemp", description = "接收Excel导入案件基本数据临时数据表")
public class BasePersonalImportExcelTemp  {

    @Id
    private String id;

    @ApiModelProperty(notes = "关联字段")
    private String relationId;

    @ApiModelProperty(notes = "批次号")
    private String batchNumber;

    @ApiModelProperty(notes = "姓名")
    @ExcelAnno(cellName = "姓名", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String personalName;

    @ApiModelProperty(notes = "证件类型")
    @ExcelAnno(cellName = "证件类型", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String certificateType;

    @ApiModelProperty(notes = "证件号")
    @ExcelAnno(cellName = "证件号", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String certificateNo;

    @ApiModelProperty(notes = "案件编号")
    @ExcelAnno(cellName = "案件编号",fieldDataType = ExcelAnno.FieldDataType.STRING,fieldType = ExcelAnno.FieldType.PERSONAL)
    private String caseNumber;

    @ApiModelProperty(notes = "帐号")
    @ExcelAnno(cellName = "帐号",fieldDataType = ExcelAnno.FieldDataType.STRING,fieldType = ExcelAnno.FieldType.PERSONAL)
    private String account;

    @ApiModelProperty(notes = "卡号")
    @ExcelAnno(cellName = "卡号",fieldDataType = ExcelAnno.FieldDataType.STRING,fieldType = ExcelAnno.FieldType.PERSONAL)
    private String cardNo1;

    @ApiModelProperty(notes = "性别")
    @ExcelAnno(cellName = "性别", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String sex;

    @ApiModelProperty(notes = "出生年月")
    @ExcelAnno(cellName = "出生年月", fieldDataType = ExcelAnno.FieldDataType.DATE, fieldType = ExcelAnno.FieldType.PERSONAL)
    @Field(type = FieldType.Date,format= DateFormat.custom,pattern = "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis")
    private Date birthday;

    @ApiModelProperty(notes = "移动电话")
    @ExcelAnno(cellName = "移动电话", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String selfPhoneNo;

    @ApiModelProperty(notes = "住宅电话")
    @ExcelAnno(cellName = "住宅电话", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String homePhoneNo;

    @ApiModelProperty(notes = "单位电话")
    @ExcelAnno(cellName = "单位电话", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String employerPhoneNo;

    @ApiModelProperty(notes = "联系类型(浦发专用)")
    @ExcelAnno(cellName = "联系类型(浦发专用)", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String relationType;

    @ApiModelProperty(notes = "地址类型(浦发专用)")
    @ExcelAnno(cellName = "地址类型(浦发专用)", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String addrType;

    @ApiModelProperty(notes = "单位名称")
    @ExcelAnno(cellName = "单位名称", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String employerName;

    @ApiModelProperty(notes = "单位地址")
    @ExcelAnno(cellName = "单位地址", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String employerAddr;

    @ApiModelProperty(notes = "户籍地址")
    @ExcelAnno(cellName = "户籍地址", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String residenceAddr;

    @ApiModelProperty(notes = "住宅地址")
    @ExcelAnno(cellName = "住宅地址", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String homeAddr;

    @ApiModelProperty(notes = "邮件地址")
    @ExcelAnno(cellName = "邮件地址", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String emailAddr;

    @ApiModelProperty(notes = "邮寄地址")
    @ExcelAnno(cellName = "邮寄地址", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String billAddr;

    @ApiModelProperty(notes =  "配偶姓名")
    @ExcelAnno(cellName = "配偶姓名", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String spouseName;

    @ApiModelProperty(notes = "配偶证件号")
    @ExcelAnno(cellName = "配偶证件号", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String spouseCertificateNo;

    @ApiModelProperty(notes = "配偶移动电话")
    @ExcelAnno(cellName = "配偶移动电话", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String spouseSelfPhoneNo;

    @ApiModelProperty(notes = "配偶住宅电话")
    @ExcelAnno(cellName = "配偶住宅电话", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String spouseHomePhoneNo;

    @ApiModelProperty(notes = "配偶单位电话")
    @ExcelAnno(cellName = "配偶单位电话", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String spouseEmployerPhoneNo;

    @ApiModelProperty(notes = "配偶单位名称")
    @ExcelAnno(cellName = "配偶单位名称", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String spouseEmployerName;

    @ApiModelProperty(notes = "配偶单位地址")
    @ExcelAnno(cellName = "配偶单位地址", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String spouseEmployerAddr;

    @ApiModelProperty(notes = "配偶户籍地址")
    @ExcelAnno(cellName = "配偶户籍地址", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String spouseResidenceAddr;

    @ApiModelProperty(notes = "配偶住宅地址")
    @ExcelAnno(cellName = "配偶住宅地址", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String spouseHomeAddr;

    @ApiModelProperty(notes =  "直系亲属姓名")
    @ExcelAnno(cellName = "直系亲属姓名", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String directName;

    @ApiModelProperty(notes = "直系亲属关系")
    @ExcelAnno(cellName = "直系亲属关系", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String directRelation;

    @ApiModelProperty(notes = "直系亲属证件号")
    @ExcelAnno(cellName = "直系亲属证件号", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String directCertificateNo;

    @ApiModelProperty(notes = "直系亲属移动电话")
    @ExcelAnno(cellName = "直系亲属移动电话", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String directSelfPhoneNo;

    @ApiModelProperty(notes = "直系亲属住宅电话")
    @ExcelAnno(cellName = "直系亲属住宅电话", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String directHomePhoneNo;

    @ApiModelProperty(notes = "直系亲属单位电话")
    @ExcelAnno(cellName = "直系亲属单位电话", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String directEmployerPhoneNo;

    @ApiModelProperty(notes = "直系亲属单位名称")
    @ExcelAnno(cellName = "直系亲属单位名称", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String directEmployerName;

    @ApiModelProperty(notes = "直系亲属单位地址")
    @ExcelAnno(cellName = "直系亲属单位地址", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String directEmployerAddr;

    @ApiModelProperty(notes = "直系亲属户籍地址")
    @ExcelAnno(cellName = "直系亲属户籍地址", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String directResidenceAddr;

    @ApiModelProperty(notes = "直系亲属住宅地址")
    @ExcelAnno(cellName = "直系亲属住宅地址", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String directHomeAddr;

    @ApiModelProperty(notes =  "联系人姓名1")
    @ExcelAnno(cellName = "联系人姓名1", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String name1;

    @ApiModelProperty(notes = "关系1")
    @ExcelAnno(cellName = "关系1", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String relation1;

    @ApiModelProperty(notes = "证件号1")
    @ExcelAnno(cellName = "证件号1", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String certificateNo1;

    @ApiModelProperty(notes = "移动电话1")
    @ExcelAnno(cellName = "移动电话1", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String selfPhoneNo1;

    @ApiModelProperty(notes = "住宅电话1")
    @ExcelAnno(cellName = "住宅电话1", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String homePhoneNo1;

    @ApiModelProperty(notes = "单位电话1")
    @ExcelAnno(cellName = "单位电话1", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String employerPhoneNo1;

    @ApiModelProperty(notes = "单位名称1")
    @ExcelAnno(cellName = "单位名称1", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String employerName1;

    @ApiModelProperty(notes = "单位地址1")
    @ExcelAnno(cellName = "单位地址1", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String employerAddr1;

    @ApiModelProperty(notes = "户籍地址1")
    @ExcelAnno(cellName = "户籍地址1", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String residenceAddr1;

    @ApiModelProperty(notes = "住宅地址1")
    @ExcelAnno(cellName = "住宅地址1", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String homeAddr1;

    @ApiModelProperty(notes = "备注1")
    @ExcelAnno(cellName = "备注1", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String remark1;

    @ApiModelProperty(notes =  "联系人姓名2")
    @ExcelAnno(cellName = "联系人姓名2", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String name2;

    @ApiModelProperty(notes = "关系2")
    @ExcelAnno(cellName = "关系2", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String relation2;

    @ApiModelProperty(notes = "证件号2")
    @ExcelAnno(cellName = "证件号2", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String certificateNo2;

    @ApiModelProperty(notes = "移动电话2")
    @ExcelAnno(cellName = "移动电话2", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String selfPhoneNo2;

    @ApiModelProperty(notes = "住宅电话2")
    @ExcelAnno(cellName = "住宅电话2", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String homePhoneNo2;

    @ApiModelProperty(notes = "单位电话2")
    @ExcelAnno(cellName = "单位电话2", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String employerPhoneNo2;

    @ApiModelProperty(notes = "单位名称2")
    @ExcelAnno(cellName = "单位名称2", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String employerName2;

    @ApiModelProperty(notes = "单位地址2")
    @ExcelAnno(cellName = "单位地址2", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String employerAddr2;

    @ApiModelProperty(notes = "户籍地址2")
    @ExcelAnno(cellName = "户籍地址2", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String residenceAddr2;

    @ApiModelProperty(notes = "住宅地址2")
    @ExcelAnno(cellName = "住宅地址2", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String homeAddr2;

    @ApiModelProperty(notes = "备注2")
    @ExcelAnno(cellName = "备注2", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String remark2;

    @ApiModelProperty(notes =  "联系人姓名3")
    @ExcelAnno(cellName = "联系人姓名3", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String name3;

    @ApiModelProperty(notes = "关系3")
    @ExcelAnno(cellName = "关系3", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String relation3;

    @ApiModelProperty(notes = "证件号3")
    @ExcelAnno(cellName = "证件号3", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String certificateNo3;

    @ApiModelProperty(notes = "移动电话3")
    @ExcelAnno(cellName = "移动电话3", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String selfPhoneNo3;

    @ApiModelProperty(notes = "住宅电话3")
    @ExcelAnno(cellName = "住宅电话3", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String homePhoneNo3;

    @ApiModelProperty(notes = "单位电话3")
    @ExcelAnno(cellName = "单位电话3", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String employerPhoneNo3;

    @ApiModelProperty(notes = "单位名称3")
    @ExcelAnno(cellName = "单位名称3", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String employerName3;

    @ApiModelProperty(notes = "单位地址3")
    @ExcelAnno(cellName = "单位地址3", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String employerAddr3;

    @ApiModelProperty(notes = "户籍地址3")
    @ExcelAnno(cellName = "户籍地址3", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String residenceAddr3;

    @ApiModelProperty(notes = "住宅地址3")
    @ExcelAnno(cellName = "住宅地址3", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String homeAddr3;

    @ApiModelProperty(notes = "备注3")
    @ExcelAnno(cellName = "备注3", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String remark3;

    @ApiModelProperty(notes =  "联系人姓名4")
    @ExcelAnno(cellName = "联系人姓名4", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String name4;

    @ApiModelProperty(notes = "关系4")
    @ExcelAnno(cellName = "关系4", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String relation4;

    @ApiModelProperty(notes = "证件号4")
    @ExcelAnno(cellName = "证件号4", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String certificateNo4;

    @ApiModelProperty(notes = "移动电话4")
    @ExcelAnno(cellName = "移动电话4", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String selfPhoneNo4;

    @ApiModelProperty(notes = "住宅电话4")
    @ExcelAnno(cellName = "住宅电话4", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String homePhoneNo4;

    @ApiModelProperty(notes = "单位电话4")
    @ExcelAnno(cellName = "单位电话4", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String employerPhoneNo4;

    @ApiModelProperty(notes = "单位名称4")
    @ExcelAnno(cellName = "单位名称4", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String employerName4;

    @ApiModelProperty(notes = "单位地址4")
    @ExcelAnno(cellName = "单位地址4", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String employerAddr4;

    @ApiModelProperty(notes = "户籍地址4")
    @ExcelAnno(cellName = "户籍地址4", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String residenceAddr4;

    @ApiModelProperty(notes = "住宅地址4")
    @ExcelAnno(cellName = "住宅地址4", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String homeAddr4;

    @ApiModelProperty(notes = "备注4")
    @ExcelAnno(cellName = "备注4", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String remark4;

    @ApiModelProperty(notes =  "联系人姓名5")
    @ExcelAnno(cellName = "联系人姓名5", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String name5;

    @ApiModelProperty(notes = "关系5")
    @ExcelAnno(cellName = "关系5", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String relation5;

    @ApiModelProperty(notes = "证件号5")
    @ExcelAnno(cellName = "证件号5", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String certificateNo5;

    @ApiModelProperty(notes = "移动电话5")
    @ExcelAnno(cellName = "移动电话5", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String selfPhoneNo5;

    @ApiModelProperty(notes = "住宅电话5")
    @ExcelAnno(cellName = "住宅电话5", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String homePhoneNo5;

    @ApiModelProperty(notes = "单位电话5")
    @ExcelAnno(cellName = "单位电话5", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String employerPhoneNo5;

    @ApiModelProperty(notes = "单位名称5")
    @ExcelAnno(cellName = "单位名称5", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String employerName5;

    @ApiModelProperty(notes = "单位地址5")
    @ExcelAnno(cellName = "单位地址5", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String employerAddr5;

    @ApiModelProperty(notes = "户籍地址5")
    @ExcelAnno(cellName = "户籍地址5", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String residenceAddr5;

    @ApiModelProperty(notes = "住宅地址5")
    @ExcelAnno(cellName = "住宅地址5", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String homeAddr5;

    @ApiModelProperty(notes = "备注5")
    @ExcelAnno(cellName = "备注5", fieldDataType = ExcelAnno.FieldDataType.STRING, fieldType = ExcelAnno.FieldType.PERSONAL)
    private String remark5;

}

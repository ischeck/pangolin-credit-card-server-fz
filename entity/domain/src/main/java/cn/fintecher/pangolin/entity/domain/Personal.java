package cn.fintecher.pangolin.entity.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

import static org.springframework.data.elasticsearch.annotations.FieldType.text;

@Data
@Document(indexName = "personal", type = "personal", shards = 1, replicas = 0)
@ApiModel(value = "personal", description = "个人信息")
public class Personal {
    @Id
    private String id;

    @ApiModelProperty(notes = "姓名")
    private String personalName;

    @ApiModelProperty(notes = "证件类型")
    private String certificateType;

    @ApiModelProperty(notes = "证件号")
    private String certificateNo;

    @ApiModelProperty(notes = "性别")
    @Field(type = text, fielddata = true)
    private String sex;

    @ApiModelProperty(notes = "出生年月")
    @Field(type = FieldType.Date)
    private Date birthday;

    @ApiModelProperty(notes = "移动电话")
    private String selfPhoneNo;

    @ApiModelProperty(notes = "住宅电话")
    private String homePhoneNo;

    @ApiModelProperty(notes = "单位电话")
    private String employerPhoneNo;

    @ApiModelProperty(notes = "单位名称")
    private String employerName;

    @ApiModelProperty(notes = "单位地址")
    private String employerAddr;

    @ApiModelProperty(notes = "户籍地址")
    private String residenceAddr;

    @ApiModelProperty(notes = "住宅地址")
    private String homeAddr;

    @ApiModelProperty(notes = "邮件地址")
    private String emailAddr;

    @ApiModelProperty(notes = "邮寄地址")
    private String billAddr;



}

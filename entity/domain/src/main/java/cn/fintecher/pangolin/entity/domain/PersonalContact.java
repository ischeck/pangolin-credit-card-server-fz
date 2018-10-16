package cn.fintecher.pangolin.entity.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ChenChang on 2017/7/12.
 */
@ApiModel(value = "PersonalContact", description = "联系人信息")
@Data
@Document(indexName = "personal_contact", type = "personal_contact", shards = 1, replicas = 0)
public class PersonalContact {
    @Id
    private String id;

    @ApiModelProperty(notes = "客户ID")
    private String personalId;

    @ApiModelProperty(notes = "姓名")
    private String name;

    @ApiModelProperty(notes = "关系")
    private String relation;

    @ApiModelProperty(notes = "证件号")
    private String certificateNo;

    @ApiModelProperty(notes = "联系人排序")
    @Field(type = FieldType.Integer)
    private Integer sort;

    @ApiModelProperty(notes = "单位名称")
    private String employerName;

    @ApiModelProperty(notes = "是否知晓此项借款")
    private String informed;

    @ApiModelProperty(notes = "是否本行")
    private String isOwnerBank;

    @ApiModelProperty(notes = "联系电话信息")
    private Set<PersonalPerCall> personalPerCalls =new HashSet<>();

    @ApiModelProperty(notes = "联系地址信息")
    private Set<PersonalPerAddr> personalPerAddrs =new HashSet<>();

    @ApiModelProperty(notes = "操作员")
    private String operator;

    @ApiModelProperty(notes = "操作时间")
    @Field(type = FieldType.Date)
    private Date operatorTime;
}

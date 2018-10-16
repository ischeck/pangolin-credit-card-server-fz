package cn.fintecher.pangolin.entity.domain;

import cn.fintecher.pangolin.common.enums.FindType;
import cn.fintecher.pangolin.common.enums.ManagementType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @author : huyanmin
 * @Description : 跟进记录实体
 * @Date : 15:49 2018/7/5
 */
@Data
@Document(indexName = "case_find_record", type = "case_find_record", shards = 1, replicas = 0)
@ApiModel(value = "caseFindRecord", description = "案件查找记录")
public class CaseFindRecord {

    @Id
    @ApiModelProperty(notes = "id")
    private String id;

    @ApiModelProperty(notes = "案件信息ID")
    private String caseId;

    @ApiModelProperty(notes = "客户ID")
    private String personalId;

    @ApiModelProperty(notes = "查找对象")
    private String target;

    @ApiModelProperty(notes = "查找对象姓名")
    private String targetName;

    @ApiModelProperty(notes = "查找方式")
    private FindType findType;

    @ApiModelProperty(notes = "是否有效")
    private ManagementType isEffect;

    @ApiModelProperty(notes = "查找记录")
    private String contant;

    @ApiModelProperty(notes = "查找时间")
    @Field(type = FieldType.Date)
    private Date findTime;

    @ApiModelProperty(notes = "操作人")
    private String operator;

    @ApiModelProperty(notes = "操作人姓名")
    private String operatorName;

    @ApiModelProperty(notes = "操作人部门")
    private String operatorDeptName;

    @ApiModelProperty(notes = "操作时间")
    @Field(type = FieldType.Date)
    private Date operatorTime;

}
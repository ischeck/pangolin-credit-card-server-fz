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
@Document(indexName = "pre_case_followup_record", type = "pre_case_followup_record", shards = 1, replicas = 0)
@ApiModel(value = "PreCaseFollowupRecord", description = "委前催计")
public class PreCaseFollowupRecord {

    @Id
    @ApiModelProperty(notes = "跟进id")
    private String id;

    @ApiModelProperty(notes = "案件信息ID")
    private String caseId;

    @ApiModelProperty(notes = "批次号")
    private String batchNumber;

    @ApiModelProperty(notes = "案件编号")
    private String caseNumber;

    @ApiModelProperty(notes = "帐号")
    private String account;

    @ApiModelProperty(notes = "卡号")
    private String cardNo;

    @ApiModelProperty(notes = "证件号")
    private String certificateNo;

    @ApiModelProperty(notes = "催记时间")
    @Field(type = FieldType.Date)
    private Date followTime;

    @ApiModelProperty(notes = "催记内容")
    private String content;


}
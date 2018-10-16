package cn.fintecher.pangolin.entity.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author:peishouwen
 * @Desc: 案件日志
 * @Date:Create in 14:39 2018/9/11
 */
@Data
@Document(indexName = "case_transfer_log", type = "case_transfer_log", shards = 1, replicas = 0)
@ApiModel(value = "CaseTransferLog", description = "案件日志")
public class CaseTransferLog implements Serializable{
    @Id
    private String id;

    @ApiModelProperty(notes = "案件ID")
    private String caseId;

    @ApiModelProperty(notes = "操作记录")
    private String operContent;

    @ApiModelProperty(notes = "操作员")
    private String userName;

    @ApiModelProperty(notes = "操作员姓名")
    private String fullName;

    @ApiModelProperty(notes = "操作时间")
    private Date operatorTime;

}

package cn.fintecher.pangolin.entity.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

/**
 * @author : BBG
 * @Description : 案件日志
 * @Date : 15:49 2018/9/3
 */
@Data
@Document(indexName = "case_operator_log", type = "case_operator_log", shards = 1, replicas = 0)
@ApiModel(value = "caseOperatorLog", description = "案件日志")
public class CaseOperatorLog {
    @Id
    @ApiModelProperty(notes = "特定字段做Md5生成")
    private String id;

    @ApiModelProperty("案件ID")
    private String caseId;

    @ApiModelProperty("内容")
    private String detail;

    @ApiModelProperty("操作时间")
    private Date operatorTime;

    @ApiModelProperty("操作人ID")
    private String operator;

    @ApiModelProperty("操作人名称")
    private String operatorName;
}

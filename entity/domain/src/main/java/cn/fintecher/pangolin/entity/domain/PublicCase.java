package cn.fintecher.pangolin.entity.domain;

import cn.fintecher.pangolin.common.enums.PublicCaseStatus;
import cn.fintecher.pangolin.entity.managentment.Operator;
import cn.fintecher.pangolin.entity.managentment.Principal;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;
import java.util.Set;

/**
 * @Author : huyanmin
 * @Description : 公共案件
 * @Date : 2018/7/16.
 */
@Data
@Document(indexName = "public_case", type = "public_case", shards = 1, replicas = 0)
@ApiModel(value = "PublicCase", description = "公共案件")
public class PublicCase {

    @Id
    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty("案件Id")
    private String caseId;

    @ApiModelProperty("客户")
    private Personal personal;

    @ApiModelProperty("批次号")
    private String batchNumber;

    @ApiModelProperty("委托方")
    private Principal principal;

    @ApiModelProperty("组织")
    private Set<String> departments;

    @ApiModelProperty("状态")
    private PublicCaseStatus publicCaseStatus;

    @ApiModelProperty(notes = "催收员")
    private Operator currentCollector;

    @ApiModelProperty("操作人")
    private String operator;

    @ApiModelProperty("操作时间")
    private Date OperatorDate;
}

package cn.fintecher.pangolin.service.domain.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.Set;

/**
 * @Author : huyanmin
 * @Description : 案件查询结果
 * @Date : 2018/7/30.
 */
@Data
public class CollectionDebtCaseResponse {

    @ApiModelProperty(notes = "客户姓名")
    private String personalName;

    @ApiModelProperty(notes = "卡号集合")
    private Set<String> cardNos;

    @ApiModelProperty(notes = "委托方名称")
    private String principalName;

    @ApiModelProperty(notes = "委案总金额")
    private Double overdueAmtTotal;

    @ApiModelProperty(notes = "最近还款金额")
    private Double latestPayAmt;

    @ApiModelProperty(notes = "还款状态")
    private String payStatus;

    @ApiModelProperty(notes = "最近还款日期")
    private Date latestPayDate;

    @ApiModelProperty(notes = "催收员")
    private String fullName;

    @ApiModelProperty(notes = "委案日期")
    private Date delegationDate;

    @ApiModelProperty(notes = "结案日期")
    private Date endCaseDate;


}

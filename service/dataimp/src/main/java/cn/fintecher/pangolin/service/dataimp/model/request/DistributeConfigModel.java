package cn.fintecher.pangolin.service.dataimp.model.request;

import cn.fintecher.pangolin.entity.managentment.Organization;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 15:32 2018/8/9
 */
@Data
public class DistributeConfigModel {

    @ApiModelProperty(notes = "用户名Id")
    private String userId;

    @ApiModelProperty(notes = "用户名")
    private String userName;

    @ApiModelProperty("姓名")
    private String fullName;

    @ApiModelProperty(notes = "当前部门ID")
    private String detaptId;

    @ApiModelProperty(notes = "当前部门名称")
    private String detaptName;

    @ApiModelProperty(notes = "分配数量上限")
    private Long limitNum;

    @ApiModelProperty(notes = "分配数量上限")
    private Double limitAmt;

    @ApiModelProperty(notes = "分配数量")
    private Long caseNumTotal=0L;

    @ApiModelProperty(notes = "分配金额")
    private Double leftAmtTotal=new Double(0);

    @ApiModelProperty(notes = "百分比")
    private Double perNum;


}

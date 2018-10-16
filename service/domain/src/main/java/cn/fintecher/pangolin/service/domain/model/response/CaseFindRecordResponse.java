package cn.fintecher.pangolin.service.domain.model.response;

import cn.fintecher.pangolin.common.enums.FindType;
import cn.fintecher.pangolin.common.enums.ManagementType;
import cn.fintecher.pangolin.common.enums.Target;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Created by BBG on 2018/8/3.
 */

@Data
public class CaseFindRecordResponse {

    @ApiModelProperty(notes = "案件信息ID")
    private String caseId;

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
    private Date findTime;

    @ApiModelProperty(notes = "操作人")
    private String operator;

    @ApiModelProperty(notes = "操作人姓名")
    private String operatorName;

    @ApiModelProperty(notes = "操作人部门")
    private String operatorDeptName;

    @ApiModelProperty(notes = "操作时间")
    private Date operatorTime;
}

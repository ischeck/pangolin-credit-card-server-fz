package cn.fintecher.pangolin.service.domain.model.response;

import cn.fintecher.pangolin.common.enums.OtherFollowType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * Created by BBG on 2018/8/4.
 */
@Data
public class CaseOtherFollowRecordResponse {

    @ApiModelProperty(notes = "案件信息ID")
    private String caseId;

    @ApiModelProperty(notes = "客户ID")
    private String personalId;

    @ApiModelProperty(notes = "跟进时间")
    private Date followTime;

    @ApiModelProperty(notes = "其他催记方式")
    private OtherFollowType type;

    @ApiModelProperty(notes = "记录")
    private String contant;

    @ApiModelProperty(notes = "操作人")
    private String operator;

    @ApiModelProperty(notes = "操作人姓名")
    private String operatorName;

    @ApiModelProperty(notes = "操作人部门")
    private String operatorDeptName;

    @ApiModelProperty(notes = "操作时间")
    private Date operatorTime;
}

package cn.fintecher.pangolin.service.domain.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @Author:huyanmin
 * @Desc: 警告信息
 * @Date:Create 2018/9/14
 */
@Data
@ApiModel(value = "CaseWarningInfo", description = "案件警告信息")
public class CaseWarningInfoRequest {

    @ApiModelProperty(notes = "案件ID")
    private String caseId;

    @ApiModelProperty(notes = "案件号")
    private String caseNumber;

    @ApiModelProperty(notes = "证件号")
    private String certificateNo;

    @ApiModelProperty(notes = "批次号")
    private String batchNumber;

    @ApiModelProperty(notes = "客户姓名")
    private String personalName;

    @ApiModelProperty(notes = "警告信息")
    private String msg;
}

package cn.fintecher.pangolin.service.dataimp.model.response;

import cn.fintecher.pangolin.common.enums.ImportDataExcelStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 13:40 2018/7/29
 */
@Data
public class ImportDataExcelRecordResponse implements Serializable {
    @ApiModelProperty("唯一标识（主键）")
    private String Id;

    @ApiModelProperty(notes = "批次号")
    private String batchNumber;

    @ApiModelProperty(notes = "委案日期")
    private Date delegationDate;

    @ApiModelProperty(notes = "结案日期")
    private Date endCaseDate;

    @ApiModelProperty(notes = "委托方名称")
    private String principalName;

    @ApiModelProperty("数据导入状态")
    private ImportDataExcelStatus importDataExcelStatus;

    @ApiModelProperty("案件数量")
    private Long caseTotal;

    @ApiModelProperty("备注文件URL")
    private String resultUrl;

    @ApiModelProperty("创建时间")
    private Date operatorTime;

    @ApiModelProperty("操作人姓名")
    private String operatorName;

}

package cn.fintecher.pangolin.service.dataimp.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Author:peishouwen
 * @Desc: 案件导入请求对象
 * @Date:Create in 11:08 2018/7/26
 */
@Data
public class BaseCaseImportExcelRequest {
    @ApiModelProperty("批次号")
    private String batchNumber;

    @ApiModelProperty(notes = "外键:文件ID")
    @NotNull(message = "{fileId.is.required}")
    private String fileId;

    @ApiModelProperty(notes = "委托方ID")
    @NotNull(message = "{principal.is.required}")
    private String principalId;

    @ApiModelProperty("委托方名称")
    @NotNull(message = "{principal.is.required}")
    private String principalName;

    @ApiModelProperty(notes = "委案日期")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date delegationDate;

    @ApiModelProperty(notes = "结案日期")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date endCaseDate;

    @ApiModelProperty(notes = "案件删除日期")
    @NotNull(message = "{deleteCaseDate.is.required}")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date deleteCaseDate;

    @ApiModelProperty("Excel 模板ID")
    @NotNull(message = "{templateId.is.required}")
    private String templateId;

}

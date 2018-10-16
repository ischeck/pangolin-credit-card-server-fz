package cn.fintecher.pangolin.service.repair.model.request;

import cn.fintecher.pangolin.common.enums.ApplyFileContent;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author : hanwannan
 * @Description : 导入资料
 * @Date : 2018/9/1.
 */
@Data
public class DataRepairImportRequest {

    @ApiModelProperty(notes = "外键:文件ID")
    @NotNull(message = "{fileId.is.required}")
    private String fileId;

    @ApiModelProperty(notes = "导入内容类型：户籍资料，户籍备注，社保资料...")
    @NotNull(message = "{importContentType.is.required}")
    private ApplyFileContent importContentType;
}

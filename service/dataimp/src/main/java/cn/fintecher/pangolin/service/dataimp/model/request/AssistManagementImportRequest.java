package cn.fintecher.pangolin.service.dataimp.model.request;

import cn.fintecher.pangolin.common.enums.ApplyType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author:huyanmin
 * @Desc: 协催案件导入
 * @Date:Create in 2018/9/7
 */
@Data
public class AssistManagementImportRequest {

    @ApiModelProperty(notes = "文件的Id")
    private String fileId;

    @ApiModelProperty(notes = "申请类型")
    private ApplyType applyType;

}

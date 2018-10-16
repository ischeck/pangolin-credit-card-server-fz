package cn.fintecher.pangolin.common.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by ChenChang on 2018/8/31.
 */
@Data
public class FileModel {

    @ApiModelProperty("文件名称")
    private String fileName;

    @ApiModelProperty("文件Id")
    private String fileId;

    @ApiModelProperty("文件路径")
    private String filePath;
}

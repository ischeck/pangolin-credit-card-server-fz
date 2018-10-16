package cn.fintecher.pangolin.common.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by ChenChang on 2017/12/22.
 */
@Data
public class UploadFile implements Serializable {
    private String id;
    @ApiModelProperty("文件名称")
    private String fileName;
    @ApiModelProperty("原始文件名")
    private String originalName;
    @ApiModelProperty("扩展名")
    private String extensionName;
    @ApiModelProperty("访问地址 相对地址")
    private String url;
}

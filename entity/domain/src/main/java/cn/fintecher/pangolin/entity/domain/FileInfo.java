package cn.fintecher.pangolin.entity.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * Created by BBG
 */

@Data
@ApiModel(value = "文件信息", description = "文件信息")
public class FileInfo {

    @ApiModelProperty(notes = "名称")
    private String fileName;

    @ApiModelProperty(notes = "路径")
    private String url;
}

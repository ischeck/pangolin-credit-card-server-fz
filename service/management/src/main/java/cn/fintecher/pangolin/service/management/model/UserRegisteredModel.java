package cn.fintecher.pangolin.service.management.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author:huyanmin
 * @Desc:
 * @Date:Create in 2018/8/21
 */
@Data
public class UserRegisteredModel {

    @ApiModelProperty(notes = "文件id")
    private String fileId;

}

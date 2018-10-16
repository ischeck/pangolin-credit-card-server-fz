package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.model.FileModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by ChenChang on 2018/8/31.
 */
@Data
public class FileSaveRequest extends FileModel{

    @ApiModelProperty("用户Id")
    private String userId;

}

package cn.fintecher.pangolin.service.management.model.response;

import cn.fintecher.pangolin.common.model.FileModel;
import cn.fintecher.pangolin.service.management.model.request.CreateUserRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huyanmin on 2018/8/29
 */
@Data
public class UserResponse extends CreateUserRequest {

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("文件路径")
    private List<FileModel> fileContent = new ArrayList<>();
}

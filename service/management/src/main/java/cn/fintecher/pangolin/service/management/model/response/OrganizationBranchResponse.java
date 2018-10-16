package cn.fintecher.pangolin.service.management.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by ChenChang on 2018/6/7
 */
@Data
public class OrganizationBranchResponse {

    @ApiModelProperty(notes = "id")
    private String id;
    @ApiModelProperty(notes = "资源名称")
    private String name;
}

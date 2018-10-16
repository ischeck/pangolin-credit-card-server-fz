package cn.fintecher.pangolin.service.domain.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by huyanmin on 2018/8/7.
 */
@Data
public class CommentRequest {

    @ApiModelProperty("案件Id")
    private String caseId;

    @ApiModelProperty("comment类型")
    private List<String> commentTypeList;

}

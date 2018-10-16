package cn.fintecher.pangolin.service.repair.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author : hanwannan
 * @Description : 批量新增关联关系
 * @Date : 2018/9/6.
 */
@Data
public class BatchCreateRelationshipRequest {

    @ApiModelProperty(notes = "关系请求列表")
    private List<CreateRelationshipRequest> createRelationshipRequestList;
}

package cn.fintecher.pangolin.service.common.model.request;

import lombok.Data;

import java.util.List;

/**
 * @Author:huyanmin
 * @Desc:
 * @Date:Create 2018/9/20
 */
@Data
public class TaskBoxDeletedRequest {

    private List<String> taskBoxIdList;
}

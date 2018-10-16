package cn.fintecher.pangolin.service.common.model.request;

import lombok.Data;

import java.util.List;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 13:35 2018/9/13
 */
@Data
public class UpdateMsgRequest {
    private List<String> ids;
}

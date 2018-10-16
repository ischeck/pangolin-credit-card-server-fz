package cn.fintecher.pangolin.service.repair.model.request;

import cn.fintecher.pangolin.entity.repair.Reply;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @Author : hanwannan
 * @Description : 回复申调
 * @Date : 2018/8/27.
 */
@Data
public class ReplyExamineStatusRequest {
    @Id
    @ApiModelProperty(notes = "特定字段做Md5生成")
    private String id;

    @ApiModelProperty(notes = "申调回复")
    private Reply reply;
}

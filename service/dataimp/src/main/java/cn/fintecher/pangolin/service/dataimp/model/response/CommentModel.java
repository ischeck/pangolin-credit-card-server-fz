package cn.fintecher.pangolin.service.dataimp.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class CommentModel {
    @ApiModelProperty("备注提醒时间")
    private Date reminderTime;

    @ApiModelProperty("备忘录列表")
    private List<CommentResponse> responses = new ArrayList<>();
}

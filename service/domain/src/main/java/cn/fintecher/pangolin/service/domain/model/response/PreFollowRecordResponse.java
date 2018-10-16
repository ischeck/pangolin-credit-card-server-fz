package cn.fintecher.pangolin.service.domain.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;

/**
 * Created by BBG on 2018/8/6.
 */
@Data
public class PreFollowRecordResponse {

    @ApiModelProperty(notes = "催记时间")
    private Date followTime;

    @ApiModelProperty(notes = "跟进内容")
    private String content;
}

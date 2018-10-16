package cn.fintecher.pangolin.entity.managentment;

import cn.fintecher.pangolin.common.enums.NoticeType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document
@ApiModel("公告")
public class Notice {

    @Id
    private String id;

    @ApiModelProperty("类型")
    private NoticeType type;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("发布时间")
    private Date operatorTime;

    @ApiModelProperty("发布人")
    private String operatorName;

}

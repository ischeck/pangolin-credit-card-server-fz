package cn.fintecher.pangolin.service.domain.model.request;

import cn.fintecher.pangolin.common.enums.OtherFollowType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by BBG on 2018/8/4.
 */
@Data
public class CaseOtherFollowModel {
    @ApiModelProperty(notes = "案件信息ID")
    private String caseId;

    @ApiModelProperty(notes = "客户ID")
    private String personalId;

    @ApiModelProperty(notes = "跟进时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date followTime;

    @ApiModelProperty(notes = "其他催记方式")
    private OtherFollowType type;

    @ApiModelProperty(notes = "记录")
    private String contant;

}

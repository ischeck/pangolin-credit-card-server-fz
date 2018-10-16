package cn.fintecher.pangolin.entity.managentment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author  huyanmin
 * @date 2018/06/29
 * @description 系统日志信息
 *
 * */

@Data
@Document
@ApiModel(value = "SystemLog",
        description = "系统日志信息")
public class SystemLog{

    @ApiModelProperty(notes = "客户端IP")
    private String clientIp;

    @ApiModelProperty(notes = "操作人")
    private String operator;

    @ApiModelProperty(notes = "创建时间")
    private Date operateTime;

    @ApiModelProperty(notes = "描述")
    private String remark;

    @ApiModelProperty(notes = "请求执行时间")
    private String exeTime;

    @ApiModelProperty(notes = "执行方法")
    private String exeMethod;

    @ApiModelProperty(notes = "操作类型")
    private String  exeType;

    @ApiModelProperty(notes = "备用字段")
    private String field;
}

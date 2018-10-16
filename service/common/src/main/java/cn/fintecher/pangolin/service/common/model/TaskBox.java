package cn.fintecher.pangolin.service.common.model;

import cn.fintecher.pangolin.common.enums.TaskBoxStatus;
import cn.fintecher.pangolin.common.enums.TaskBoxType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document
@ApiModel(value = "taskBox", description = "任务盒子表")
public class TaskBox  {

    @Id
    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "类型")
    private TaskBoxType taskBoxType;

    @ApiModelProperty(value = "任务状态")
    private TaskBoxStatus taskBoxStatus;

    @ApiModelProperty(value = "开始时间")
    private Date beginDate;

    @ApiModelProperty(value = "结束时间")
    private Date endDate;

    @ApiModelProperty(value = "任务描述")
    private String taskDescribe;

    @ApiModelProperty(value = "导出文件URL地址")
    private String fileId;

    @ApiModelProperty(value = "导出文件名称")
    private String fileName;

    @ApiModelProperty(value = "操作人")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private Date operatorTime;

}

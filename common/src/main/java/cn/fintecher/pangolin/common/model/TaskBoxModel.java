package cn.fintecher.pangolin.common.model;

import cn.fintecher.pangolin.common.enums.TaskBoxStatus;
import cn.fintecher.pangolin.common.enums.TaskBoxType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "taskBox", description = "任务盒子表")
public class TaskBoxModel {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "类型")
    private TaskBoxType taskBoxType;

    @ApiModelProperty(value = "任务状态")
    private TaskBoxStatus taskBoxStatus;

    @ApiModelProperty(value = "任务描述")
    private String taskDescribe;

    @ApiModelProperty(value = "开始时间")
    private Date beginDate;

    @ApiModelProperty(value = "结束时间")
    private Date endDate;

    @ApiModelProperty(value = "导出文件Id")
    private String fileId;

    @ApiModelProperty(value = "导出文件名称")
    private String fileName;

    @ApiModelProperty(value = "操作人")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private Date operatorTime;

}

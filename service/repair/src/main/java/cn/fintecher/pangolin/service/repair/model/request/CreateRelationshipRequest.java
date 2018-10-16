package cn.fintecher.pangolin.service.repair.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author : hanwannan
 * @Description : 新增关联关系
 * @Date : 2018/8/27.
 */
@Data
public class CreateRelationshipRequest {

    @ApiModelProperty(notes = "姓名")
    private String name;

    @ApiModelProperty("身份证号")
    private String idNo;

    @ApiModelProperty("关系")
    private String relation;

    @ApiModelProperty("关系人姓名")
    private String relationPersonName;

    @ApiModelProperty("关系人身份证号")
    private String relationPersonIdNo;

    @ApiModelProperty("系统筛查")
    private String systemFilterQuery;

    @ApiModelProperty("数据来源")
    private String dataSource;
}

package cn.fintecher.pangolin.service.repair.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author : hanwannan
 * @Description : 数据修复-导入特调资料
 * @Date : 2018/8/23.
 */
@Data
public class RelationShipDataModel {

    @ApiModelProperty("身份证")
    private String idNo;

    @ApiModelProperty("关系")
    private String relation;

    @ApiModelProperty("关系人姓名")
    private String relationPersonName;

    @ApiModelProperty("关系人身份证号")
    private String relationPersonIdNo;

    @ApiModelProperty("类型")
    private String type;

    @ApiModelProperty("座机")
    private String landLinePhone;

    @ApiModelProperty("手机")
    private String mobile;

    @ApiModelProperty("地址")
    private String address;

    @ApiModelProperty("备注")
    private String remark;
}

package cn.fintecher.pangolin.service.repair.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author : hanwannan
 * @Description : 数据修复-导入特调资料
 * @Date : 2018/8/23.
 */
@Data
public class SpecialTransferDataModel {


    @ApiModelProperty(notes = "相关证件")
    private List<String> fileIds = new ArrayList<>();

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("计生地区")
    private String familyPlanningArea;

}

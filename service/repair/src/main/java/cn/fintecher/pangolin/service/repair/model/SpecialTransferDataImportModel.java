package cn.fintecher.pangolin.service.repair.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

/**
 * @Author : hanwannan
 * @Description : 数据修复-导入特调资料
 * @Date : 2018/8/23.
 */
@Data
public class SpecialTransferDataImportModel {

    @Id
    @ApiModelProperty(notes = "特定字段做Md5生成")
    private String id;

    @ApiModelProperty(notes = "姓名")
    private String name;

    @ApiModelProperty("身份证")
    private String idNo;

    @ApiModelProperty("银行")
    private String bank;

    @ApiModelProperty("申调地区")
    private String applyTransferArea;

    @ApiModelProperty("文件名")
    private String fileName;

    @ApiModelProperty("与本人关系")
    private String relationship;

    @ApiModelProperty("证件类型")
    private String type;

    @ApiModelProperty("户籍地区")
    private String kosekiArea;

    @ApiModelProperty("查询日期")
    private Date queryDate;

    @ApiModelProperty("备注/金额")
    private String remark;

    @ApiModelProperty("导入日期")
    private Date importDate;
}

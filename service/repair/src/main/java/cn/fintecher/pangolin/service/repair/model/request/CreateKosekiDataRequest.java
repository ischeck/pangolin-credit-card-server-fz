package cn.fintecher.pangolin.service.repair.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author : hanwannan
 * @Description : 新增户籍资料
 * @Date : 2018/8/27.
 */
@Data
public class CreateKosekiDataRequest {

    @ApiModelProperty(notes = "姓名")
    private String name;

    @ApiModelProperty("身份证号")
    private String idNo;

    @ApiModelProperty("银行")
    private String bank;

    @ApiModelProperty("户籍地区")
    private String kosekiArea;

    @ApiModelProperty("户籍")
    private String koseki;

    @ApiModelProperty("申调地区")
    private String applyTransferArea;

    @ApiModelProperty("申调时间")
    private Date applyTransferTime;

    @ApiModelProperty("服务处所")
    private String serviceSpace;

    @ApiModelProperty("新地址")
    private String newAddress;

    @ApiModelProperty("联系方式")
    private String contact;

    @ApiModelProperty("曾用名")
    private String usedName;

    @ApiModelProperty("备注")
    private String remark;
}

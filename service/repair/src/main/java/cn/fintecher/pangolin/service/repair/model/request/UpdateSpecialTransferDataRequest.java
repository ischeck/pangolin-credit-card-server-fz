package cn.fintecher.pangolin.service.repair.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * @Author : hanwannan
 * @Description : 更新特调资料
 * @Date : 2018/8/27.
 */
@Data
public class UpdateSpecialTransferDataRequest {
    @Id
    @ApiModelProperty(notes = "特定字段做Md5生成")
    private String id;

    @ApiModelProperty(notes = "姓名")
    private String name;

    @ApiModelProperty("身份证号")
    private String idNo;

    @ApiModelProperty("银行")
    private String bank;

    @ApiModelProperty("申调地区")
    private String applyTransferArea;

    @ApiModelProperty("特调地区")
    private String specialTransferArea;

    @ApiModelProperty("申调时间")
    private Date applyTransferTime;

    @ApiModelProperty("手机")
    private String mobile;

    @ApiModelProperty("备注")
    private String remark;

}

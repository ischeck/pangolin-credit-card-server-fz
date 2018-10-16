package cn.fintecher.pangolin.entity.repair;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

/**
 * @Author : hanwannan
 * @Description : 数据修复-支付宝信息
 * @Date : 2018/8/23.
 */
@Data
@Document(indexName = "alipay_info", type = "alipay_info", shards = 1, replicas = 0)
@ApiModel(value = "AlipayInfo", description = "支付宝信息")
public class AlipayInfo {

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

    @ApiModelProperty("申调时间")
    private Date applyTransferTime;

    @ApiModelProperty("座机")
    private String landLinePhone;

    @ApiModelProperty("手机")
    private String mobile;

    @ApiModelProperty("地址")
    private String address;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("导入日期")
    private Date importDate;
}

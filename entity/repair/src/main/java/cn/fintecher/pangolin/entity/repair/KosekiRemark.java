package cn.fintecher.pangolin.entity.repair;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

/**
 * @Author : hanwannan
 * @Description : 数据修复-户籍备注
 * @Date : 2018/8/23.
 */
@Data
@Document(indexName = "koseki_remark", type = "koseki_remark", shards = 1, replicas = 0)
@ApiModel(value = "KosekiRemark", description = "户籍备注")
public class KosekiRemark {

    @Id
    @ApiModelProperty(notes = "特定字段做Md5生成")
    private String id;

    @ApiModelProperty(notes = "姓名")
    private String name;

    @ApiModelProperty("身份证号")
    private String idNo;

    @ApiModelProperty("银行")
    private String bank;

    @ApiModelProperty("户籍地区")
    private String kosekiArea;

    @ApiModelProperty("户籍地址")
    private String kosekiAddress;

    @ApiModelProperty("申调地区")
    private String applyTransferArea;

    @ApiModelProperty("申调时间")
    private Date applyTransferTime;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("导入日期")
    private Date importDate;
}

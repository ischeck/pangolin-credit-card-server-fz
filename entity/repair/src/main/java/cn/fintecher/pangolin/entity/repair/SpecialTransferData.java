package cn.fintecher.pangolin.entity.repair;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;
import java.util.List;

/**
 * @Author : hanwannan
 * @Description : 数据修复-特调资料
 * @Date : 2018/8/23.
 */
@Data
@Document(indexName = "special_transfer_data", type = "special_transfer_data", shards = 1, replicas = 0)
@ApiModel(value = "SpecialTransferData", description = "特调资料")
public class SpecialTransferData {

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

    @ApiModelProperty(notes = "相关证件")
    private List<Credential> credentialSet;

    @ApiModelProperty("户籍地区")
    private String kosekiArea;

    @ApiModelProperty("查询日期")
    private Date queryDate;
    
    @ApiModelProperty("导入日期")
    private Date importDate;
}

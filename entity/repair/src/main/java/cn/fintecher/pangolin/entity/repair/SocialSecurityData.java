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
 * @Description : 数据修复-社保资料
 * @Date : 2018/8/23.
 */
@Data
@Document(indexName = "social_security_data", type = "social_security_data", shards = 1, replicas = 0)
@ApiModel(value = "SocialSecurityData", description = "社保资料")
public class SocialSecurityData {

    @Id
    @ApiModelProperty(notes = "特定字段做Md5生成")
    private String id;

    @ApiModelProperty(notes = "姓名")
    private String name;

    @ApiModelProperty("身份证号")
    private String idNo;

    @ApiModelProperty("银行")
    private String bank;

    @ApiModelProperty("工作单位")
    private String workUnit;

    @ApiModelProperty("公司地址")
    private String companyAddress;

    @ApiModelProperty("公司电话")
    private String companyPhone;

    @ApiModelProperty("户籍地址")
    private String kosekiAddress;

    @ApiModelProperty("申调地区")
    private String applyTransferArea;

    @ApiModelProperty("申调时间")
    private Date applyTransferTime;

    @ApiModelProperty("社保号")
    private String socialSecurityNo;

    @ApiModelProperty("参保时间")
    private Date attendSecurityTime;

    @ApiModelProperty("参保状态")
    private String attendSecurityStatus;

    @ApiModelProperty("最近缴纳时间")
    private Date latelyPayTime;

    @ApiModelProperty("最近缴纳基数")
    private Double latelyPayBase;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("导入日期")
    private Date importDate;

    @ApiModelProperty("上传资料Id")
    private List<String> fileIdList;
}

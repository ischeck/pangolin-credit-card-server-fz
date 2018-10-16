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
 * @Description : 数据修复-通讯资料
 * @Date : 2018/8/23.
 */
@Data
@Document(indexName = "communication_data", type = "communication_data", shards = 1, replicas = 0)
@ApiModel(value = "CommunicationData", description = "通讯资料")
public class CommunicationData {

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

    @ApiModelProperty("导入日期")
    private Date importDate;

    @ApiModelProperty("上传资料Id")
    private List<String> fileId;
}

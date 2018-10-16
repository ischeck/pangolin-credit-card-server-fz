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
 * @Description : 数据修复-户籍资料
 * @Date : 2018/8/23.
 */
@Data
@Document(indexName = "koseki_data", type = "koseki_data", shards = 1, replicas = 0)
@ApiModel(value = "KosekiData", description = "户籍资料")
public class KosekiData {

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

    @ApiModelProperty("导入日期, 此处的导入时间和新增时间都在这个字段记录")
    private Date importDate;

    @ApiModelProperty("上传资料Id")
    private List<String> fileId;
}

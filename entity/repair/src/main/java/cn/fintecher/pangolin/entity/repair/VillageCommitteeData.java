package cn.fintecher.pangolin.entity.repair;

import cn.fintecher.pangolin.common.enums.VillageCommitteeStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;
import java.util.List;

/**
 * @Author : hanwannan
 * @Description : 数据修复-村委资料
 * @Date : 2018/8/23.
 */
@Data
@Document(indexName = "village_committee_data", type = "village_committee_data", shards = 1, replicas = 0)
@ApiModel(value = "VillageCommitteeData", description = "村委资料")
public class VillageCommitteeData {

    @Id
    @ApiModelProperty(notes = "特定字段做Md5生成")
    private String id;

    @ApiModelProperty(notes = "存放合并的地址")
    private String address;

    @ApiModelProperty(notes = "省份")
    private String province;

    @ApiModelProperty(notes = "城市")
    private String city;

    @ApiModelProperty(notes = "区/县")
    private String area;

    @ApiModelProperty(notes = "镇/乡")
    private String town;

    @ApiModelProperty(notes = "村/居委会")
    private String village;

    @ApiModelProperty("地区码")
    private String areaCode;

    @ApiModelProperty("联系人")
    private String linkman;

    @ApiModelProperty("查询人")
    private String queryMan;

    @ApiModelProperty("查询日期")
    private Date queryDate;

    @ApiModelProperty("职务")
    private String position;

    @ApiModelProperty("办公电话")
    private String officePhone;

    @ApiModelProperty("手机")
    private String mobile;

    @ApiModelProperty("家庭电话")
    private String homePhone;

    @ApiModelProperty("登记信息")
    private String registerInfo;

    @ApiModelProperty("状态")
    private VillageCommitteeStatus status;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("导入日期")
    private Date importDate;

    @ApiModelProperty("文件查看")
    private List<String> fileIdList;
}

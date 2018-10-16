package cn.fintecher.pangolin.service.repair.model;

import cn.fintecher.pangolin.common.enums.VillageCommitteeStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;
import java.util.List;

/**
 * @Author : huyanmin
 * @Description : 数据修复-村委资料model
 * @Date : 2018/9/26.
 */
@Data
public class VillageCommitteeModel {

    @Id
    @ApiModelProperty(notes = "特定字段做Md5生成")
    private String id;

    @ApiModelProperty(notes = "村委")
    private String address;

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

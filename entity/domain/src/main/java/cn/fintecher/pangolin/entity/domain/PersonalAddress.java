package cn.fintecher.pangolin.entity.domain;

import cn.fintecher.pangolin.common.enums.AssistFlag;
import cn.fintecher.pangolin.common.enums.Source;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 17:18 2018/7/11
 */
@Data
@Document(indexName = "personal_address", type = "personal_address", shards = 1, replicas = 0)
@ApiModel(value = "personalAddress", description = "关系人地址信息")
public class PersonalAddress {
    @Id
    private String id;

    @ApiModelProperty(notes = "客户ID")
    private String personalId;

    @ApiModelProperty(notes = "姓名")
    private String name;

    @ApiModelProperty(notes = "证件号")
    private String certificateNo;

    @ApiModelProperty(notes = "联系人单位")
    private String employerName;

    @ApiModelProperty(notes = "关系")
    private String relation;



    @ApiModelProperty(notes = "操作员")
    private String operator;

    @ApiModelProperty(notes = "操作时间")
    private Date operatorTime;

    @ApiModelProperty("备注")
    private String remark;

}

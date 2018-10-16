package cn.fintecher.pangolin.entity.domain;

import cn.fintecher.pangolin.common.enums.Source;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 15:18 2018/9/6
 */
@Data
@ApiModel(value = "PersonalPerCall", description = "联系电话子项")
public class PersonalPerCall {

    @Id
    @ApiModelProperty(notes = "id")
    private String id;

    @ApiModelProperty(notes = "电话状态")
    private String phoneState;

    @ApiModelProperty(notes = "电话类型")
    private String phoneType;

    @ApiModelProperty(notes = "电话号码")
    private String phoneNo;

    @ApiModelProperty(notes = "联络结果")
    private String contactResult;

    @ApiModelProperty(notes = "来源")
    private Source source;

    @ApiModelProperty(notes = "备注")
    private String remark;

    @ApiModelProperty(notes = "拨打电话次数")
    private Integer dialPhoneCount = 0;

    @ApiModelProperty(notes = "短信发送次数")
    @Field(type= FieldType.Integer)
    private Integer sendMessageCount = 0;

}

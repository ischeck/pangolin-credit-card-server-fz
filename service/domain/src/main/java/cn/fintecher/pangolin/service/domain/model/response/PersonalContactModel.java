package cn.fintecher.pangolin.service.domain.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * Created by huyanmin 2018/07/31
 */

@Data
public class PersonalContactModel {

    @ApiModelProperty(notes = "联系人姓名")
    private String name;

    @ApiModelProperty(notes = "身份证号码")
    private String certificateNo;

    @ApiModelProperty(notes = "关系")
    private String relation;

    @ApiModelProperty(notes = "单位名称")
    private String employerName;

    @ApiModelProperty(notes = "电话状态")
    private String phoneState;

    @ApiModelProperty(notes = "拨打电话次数")
    private Integer dialPhoneCount = 0;

    @ApiModelProperty(notes = "短信发送次数")
    private Integer sendMessageCount = 0;

    @ApiModelProperty(notes = "联络结果")
    private String contactResult;

    @ApiModelProperty(notes = "联系人排序")
    private String sort;

    @ApiModelProperty(notes = "操作时间")
    @Field(type = FieldType.Date)
    private Date operatorTime;
}

package cn.fintecher.pangolin.entity.domain;

import cn.fintecher.pangolin.common.enums.AssistFlag;
import cn.fintecher.pangolin.common.enums.AssistStatus;
import cn.fintecher.pangolin.entity.managentment.Operator;
import cn.fintecher.pangolin.entity.managentment.Principal;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @Author : huyanmin
 * @Description : 外访协助案件池
 * @Date : 2018/7/16.
 */
@Data
@Document(indexName = "assist_collection_case", type = "assist_collection_case", shards = 1, replicas = 0)
@ApiModel(value = "AssistCollectionCase", description = "外访协助案件池")
public class AssistCollectionCase {

    @Id
    @ApiModelProperty(notes = "特定字段做Md5生成")
    private String id;

    @ApiModelProperty(notes = "案件Id")
    private String caseId;

    @ApiModelProperty(notes = "催计数")
    @Field(type = FieldType.Integer)
    private Integer collectionRecordCount=0; 

    @ApiModelProperty("姓名")
    private String personalName;

    @ApiModelProperty("证件号")
    private String idCard;

    @ApiModelProperty("批次号")
    private String batchNumber;

    @ApiModelProperty(notes = "委托方信息")
    private Principal principal;

    @ApiModelProperty("卡号")
    private Set<CardInformation> cardInformationSet;

    @ApiModelProperty(notes = "协助标识")
    private AssistFlag assistFlag;

    @ApiModelProperty(notes = "协助状态")
    private AssistStatus assistStatus;

    @ApiModelProperty("协催地址")
    private String addressDetail;

    @ApiModelProperty("地址类型")
    private String addressType;

    @ApiModelProperty("关系")
    private String relation;

    @ApiModelProperty("地址Id")
    private String personalAddressId;

    @ApiModelProperty("联系电话Id")
    private String personalContactId;

    @ApiModelProperty(notes = "当前外放协助催收员")
    private Operator currentCollector;

    @ApiModelProperty(notes = "上一个外放协助催收员")
    private Operator latelyOurCollector;

    @ApiModelProperty(notes = "部门ID用于权限判断")
    private Set<String> departments;

    @ApiModelProperty("申请原因")
    private String applyReason;

    @ApiModelProperty(notes = "外访时间")
    private Date applyDate;

    @ApiModelProperty("申请人姓名")
    private String applyRealName;

    @ApiModelProperty(notes = "操作员")
    private String operator;

    @ApiModelProperty(notes = "操作时间")
    private Date operatorTime;

}

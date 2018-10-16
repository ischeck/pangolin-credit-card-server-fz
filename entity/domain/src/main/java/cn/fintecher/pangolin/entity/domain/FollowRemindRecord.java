package cn.fintecher.pangolin.entity.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @Author:gaobeibei
 * @Desc: 跟进提醒记录
 * @Date:Create in 15:36 2018/9/18
 */
@Data
@Document(indexName = "follow_remind_record", type = "follow_remind_record", shards = 1, replicas = 0)
@ApiModel(value = "FollowRemindRecord", description = "跟进提醒记录")
public class FollowRemindRecord {
    @Id
    private String id;

    @ApiModelProperty("案件ID")
    private String caseId;

    @ApiModelProperty("提醒日期")
    @Field(type = FieldType.Date)
    private Date date;


}

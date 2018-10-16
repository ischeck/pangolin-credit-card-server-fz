package cn.fintecher.pangolin.entity.repair;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * @Author : hanwannan
 * @Description : 数据修复-关联关系
 * @Date : 2018/8/23.
 */
@Data
@Document(indexName = "relationship", type = "relationship", shards = 1, replicas = 0)
@ApiModel(value = "Relationship", description = "关联关系")
public class Relationship {

    @Id
    @ApiModelProperty(notes = "特定字段做Md5生成")
    private String id;

    @ApiModelProperty(notes = "姓名")
    private String name;

    @ApiModelProperty("身份证号")
    private String idNo;

    @ApiModelProperty("关系")
    private String relation;

    @ApiModelProperty("关系人姓名")
    private String relationPersonName;

    @ApiModelProperty("关系人身份证号")
    private String relationPersonIdNo;

    @ApiModelProperty("系统筛查")
    private String systemFilterQuery;

    @ApiModelProperty("数据来源")
    private String dataSource;
    
    @ApiModelProperty("导入日期")
    private Date importDate;

}

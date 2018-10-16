package cn.fintecher.pangolin.entity.repair;

import cn.fintecher.pangolin.common.enums.ApplyFileContent;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @Author:hanwannan
 * @Desc:数据修复导入记录
 * @Date:Create in 14:34 2018/9/1
 */
@Data
@Document(indexName = "data_repair_import_record", type = "data_repair_import_record", shards = 1, replicas = 0)
@ApiModel(value = "DataRepairImportRecord", description = "数据修复导入记录")
public class DataRepairImportRecord {

    @ApiModelProperty("唯一标识（主键）")
    @Id
    private String Id;

    @ApiModelProperty(notes = "外键:文件ID")
    private String fileId;

    @ApiModelProperty("备注文件URL")
    private String resultUrl;

    @ApiModelProperty(notes = "导入内容类型：户籍资料，户籍备注，社保资料...")
    private ApplyFileContent importContentType;

    @ApiModelProperty("导入数据数量")
    private Long dataTotal;

    @ApiModelProperty("创建时间")
    @Field(type = FieldType.Date)
    private Date operatorTime;

    @ApiModelProperty("操作人姓名")
    private String operatorName;

    @ApiModelProperty("操作人")
    private String operatorUserName;

}

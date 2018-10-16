package cn.fintecher.pangolin.common.model;

import cn.fintecher.pangolin.common.enums.ManagementType;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @Author:peishouwen
 * @Desc: 无匹配信息
 * @Date:Create in 13:52 2018/9/27
 */
@Data
public class RemarkModel {

    @Field(type= FieldType.text)
    private String key;
    @Field(type= FieldType.text)
    private String value;

    private ManagementType hideFlag=ManagementType.NO;

    private Integer sort;
}

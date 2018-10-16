package cn.fintecher.pangolin.entity.managentment;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * 中国行政区域
 */
@Data
@Document
public class District implements Serializable {
    @Id
    private String id;
    private String name;
    private String parent;
    private String initial;
    private String initials;
    private String pinyin;
    private String suffix;
    private String code;
    private String order;
}

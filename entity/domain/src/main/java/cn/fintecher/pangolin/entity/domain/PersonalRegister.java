package cn.fintecher.pangolin.entity.domain;

import cn.fintecher.pangolin.common.enums.Marital;
import cn.fintecher.pangolin.common.enums.Sex;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

@Data
@Document(indexName = "personal_register", type = "personal_register", shards = 1, replicas = 0)
@ApiModel(value = "personalRegister", description = "个人户籍资料")
public class PersonalRegister {
    @Id
    private String id;

    @ApiModelProperty(notes = "姓名")
    private String name;

    @ApiModelProperty(notes = "性别")
    private Sex sex;

    @ApiModelProperty(notes = "婚姻状态")
    private Marital marital;

    @ApiModelProperty(notes = "教育程度")
    private String education;

    @ApiModelProperty(notes = "毕业学校")
    private String school;

    @ApiModelProperty(notes = "出生日期")
    private Date birthday;

    @ApiModelProperty(notes = "身份证号码")
    private String idCard;

    @ApiModelProperty(notes = "身份证有效期")
    private Integer idCardValidityPeriod;

    @ApiModelProperty(notes = "身份证发证机关")
    private String idCardIssuingAuthority;

    @ApiModelProperty(notes = "身份证地址")
    private String idCardAddress;


}

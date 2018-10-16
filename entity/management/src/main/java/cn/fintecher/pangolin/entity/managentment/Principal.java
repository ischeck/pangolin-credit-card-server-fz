package cn.fintecher.pangolin.entity.managentment;

import cn.fintecher.pangolin.common.enums.PrincipalState;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @Author huyanmin
 * @Date 2018/06/26
 * @Description 委托方
 */

@Data
@Document
public class Principal {

    @Id
    @ApiModelProperty(notes = "id标识")
    private String id;

    @ApiModelProperty(notes = "委托方")
    private String principalName;

    @ApiModelProperty(notes = "联系电话")
    private String phone;

    @ApiModelProperty(notes = "创建时间")
    private Date operatorTime;

    @ApiModelProperty(notes = "操作人")
    private String user;

    @ApiModelProperty(notes = "是否删除 启用/禁用")
    private PrincipalState state;

    @Size(max = 1000, message = "备注不能超过1000个字符")
    @ApiModelProperty(notes = "备注")
    private String remark;
}

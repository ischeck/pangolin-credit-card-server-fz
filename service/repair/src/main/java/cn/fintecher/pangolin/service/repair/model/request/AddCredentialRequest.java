package cn.fintecher.pangolin.service.repair.model.request;

import cn.fintecher.pangolin.entity.repair.Credential;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

import org.springframework.data.annotation.Id;

/**
 * @Author : hanwannan
 * @Description : 更新计生资料
 * @Date : 2018/8/27.
 */
@Data
public class AddCredentialRequest {
    @Id
    @ApiModelProperty(notes = "特定字段做Md5生成")
    private String id;

    @ApiModelProperty(notes = "相关证件")
    private List<Credential> credentialSet;
}

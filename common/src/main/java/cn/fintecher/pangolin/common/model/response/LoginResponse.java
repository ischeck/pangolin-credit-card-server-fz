package cn.fintecher.pangolin.common.model.response;

import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.model.OrganizationModel;
import cn.fintecher.pangolin.common.model.ResourceModel;
import cn.fintecher.pangolin.common.model.RoleModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Created by ChenChang on 2017/12/20.
 */
@Data
public class LoginResponse implements Serializable {
    private OperatorModel user;
    private OrganizationModel organizationModel;
    private String token;
}

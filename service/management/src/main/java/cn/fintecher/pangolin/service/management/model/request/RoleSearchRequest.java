package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.enums.RoleState;
import cn.fintecher.pangolin.common.web.MongoSearchRequest;
import cn.fintecher.pangolin.entity.managentment.QRole;
import com.querydsl.core.BooleanBuilder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

@Data
public class RoleSearchRequest extends MongoSearchRequest {


    @ApiModelProperty(notes = "角色名称")
    private String roleName;

    @ApiModelProperty(notes = "状态")
    private RoleState status;

    @Override
    public BooleanBuilder generateQueryBuilder() {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QRole qRole = QRole.role;
        if (Objects.nonNull(this.roleName)) {
            booleanBuilder.and(qRole.name.contains(this.roleName));
        }

        if (Objects.nonNull(this.status)) {
            booleanBuilder.and(qRole.state.eq(this.status));
        }
        return booleanBuilder;
    }


}

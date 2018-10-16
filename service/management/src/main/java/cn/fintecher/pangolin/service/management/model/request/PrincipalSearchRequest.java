package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.enums.PrincipalState;
import cn.fintecher.pangolin.common.enums.PrincipalType;
import cn.fintecher.pangolin.common.web.MongoSearchRequest;
import cn.fintecher.pangolin.entity.managentment.QPrincipal;
import com.querydsl.core.BooleanBuilder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

@Data
public class PrincipalSearchRequest extends MongoSearchRequest {

    @ApiModelProperty(notes = "委托方名称")
    private String principalName;

    @ApiModelProperty(notes = "手机号")
    private String phone;

    @ApiModelProperty(notes = "机构类型")
    private PrincipalType type;


    @Override
    public BooleanBuilder generateQueryBuilder() {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QPrincipal qPrincipal = QPrincipal.principal;
        if (Objects.nonNull(this.principalName)) {
            booleanBuilder.and(qPrincipal.principalName.contains(this.principalName));
        }

        if (Objects.nonNull(this.phone)) {
            booleanBuilder.and(qPrincipal.phone.eq(this.phone));
        }

        booleanBuilder.and(qPrincipal.state.eq(PrincipalState.ENABLED));
        return booleanBuilder;
    }


}

package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.enums.UserState;
import cn.fintecher.pangolin.common.web.MongoSearchRequest;
import cn.fintecher.pangolin.entity.managentment.QUser;
import com.querydsl.core.BooleanBuilder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

@Data
public class UserSearchRequest extends MongoSearchRequest {

    @ApiModelProperty(notes = "姓名")
    private String fullName;

    @ApiModelProperty(notes = "工号")
    private String employeeNumber;

    @ApiModelProperty(notes = "状态")
    private UserState status;

    @ApiModelProperty(notes = "组织ID")
    private String organizationId;


    @Override
    public BooleanBuilder generateQueryBuilder() {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QUser qUser = QUser.user;
        if (Objects.nonNull(this.fullName)) {
            booleanBuilder.and(qUser.fullName.contains(this.fullName));
        }
        if (Objects.nonNull(this.status)) {
            booleanBuilder.and(qUser.state.eq(this.status));
        }
        if (Objects.nonNull(this.employeeNumber)) {
            booleanBuilder.and(qUser.employeeNumber.eq(this.employeeNumber));
        }

        return booleanBuilder;
    }


}

package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.enums.OperatorState;
import cn.fintecher.pangolin.common.web.MongoSearchRequest;
import cn.fintecher.pangolin.entity.managentment.QOperator;
import cn.fintecher.pangolin.service.management.service.OrganizationService;
import com.querydsl.core.BooleanBuilder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

@Data
public class OperatorSearchRequest extends MongoSearchRequest {
    @ApiModelProperty(notes = "姓名")
    private String fullName;

    @ApiModelProperty(notes = "用户名")
    private String userName;

    @ApiModelProperty(notes = "工号")
    private String employeeNumber;

    @ApiModelProperty(notes = "状态")
    private OperatorState status;

    @ApiModelProperty(notes = "组织ID")
    private String organizationId;

    @ApiModelProperty(notes = "角色ID")
    private String roleId;


    @Override
    public BooleanBuilder generateQueryBuilder() {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QOperator qOperator = QOperator.operator;
        if (Objects.nonNull(this.fullName)) {
            booleanBuilder.and(qOperator.fullName.contains(this.fullName));
        }

        if (Objects.nonNull(this.userName)) {
            booleanBuilder.and(qOperator.username.contains(this.userName));
        }

        if (Objects.nonNull(this.status)) {
            booleanBuilder.and(qOperator.state.eq(this.status));
        }

        if(Objects.nonNull(this.roleId)){
            booleanBuilder.and(QOperator.operator.role.contains(this.roleId));
        }
        return booleanBuilder;
    }


}

package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.enums.SysParamState;
import cn.fintecher.pangolin.common.web.MongoSearchRequest;
import cn.fintecher.pangolin.entity.managentment.QSysParam;
import com.querydsl.core.BooleanBuilder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * @Author : huyanmin
 * @Description : 系统参数请求
 * @Date : 2018/6/27.
 */
@Data
public class SysParamRequest extends MongoSearchRequest {

    @ApiModelProperty(notes = "id")
    @NotNull(message = "{id.is.required}")
    private String id;

    @ApiModelProperty(notes = "参数名称")
    private String code;

    @ApiModelProperty(notes = "参数是否启用")
    private SysParamState state;

    @ApiModelProperty(notes = "参数值")
    private String value;

    @Override
    public BooleanBuilder generateQueryBuilder() {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QSysParam qSysParam = QSysParam.sysParam;
        if (Objects.nonNull(this.code)) {
            booleanBuilder.and(qSysParam.code.eq(this.code));
        }
        if (Objects.nonNull(this.state)) {
            booleanBuilder.and(qSysParam.state.eq(this.state));
        }

        return booleanBuilder;
    }

}

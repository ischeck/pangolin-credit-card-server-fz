package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.enums.ManagementType;
import cn.fintecher.pangolin.common.enums.OtherTemplateType;
import cn.fintecher.pangolin.common.web.MongoSearchRequest;
import cn.fintecher.pangolin.entity.managentment.QTemplate;
import com.querydsl.core.BooleanBuilder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

@Data
public class TemplateSearchRequest extends MongoSearchRequest {

    @ApiModelProperty("委托方ID")
    private String principalId;

    @ApiModelProperty(notes = "模板名称")
    private String templateName;

    @ApiModelProperty(notes = "模板类型")
    private OtherTemplateType type;

    @ApiModelProperty("是否启用")
    private ManagementType isEnabled;


    @Override
    public BooleanBuilder generateQueryBuilder() {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QTemplate qTemplate = QTemplate.template;
        if (Objects.nonNull(this.principalId)) {
            booleanBuilder.and(qTemplate.principalId.eq(this.principalId));
        }
        if(Objects.nonNull(this.templateName)){
            booleanBuilder.and(qTemplate.templateName.eq(this.templateName));
        }
        if(Objects.nonNull(this.type)){
            booleanBuilder.and(qTemplate.type.eq(this.type));
        }
        if(Objects.nonNull(this.isEnabled)){
            booleanBuilder.and(qTemplate.isEnabled.eq(this.isEnabled));
        }
        return booleanBuilder;
    }
}

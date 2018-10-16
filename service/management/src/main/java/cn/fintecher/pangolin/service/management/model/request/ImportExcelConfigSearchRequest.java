package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.enums.TemplateType;
import cn.fintecher.pangolin.common.web.MongoSearchRequest;
import cn.fintecher.pangolin.entity.managentment.QImportExcelConfig;
import com.querydsl.core.BooleanBuilder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

@Data
public class ImportExcelConfigSearchRequest extends MongoSearchRequest {
    @ApiModelProperty(notes = "委托方ID")
    private String principalId;

    @ApiModelProperty(notes = "模板名称")
    private String templateDataName;

    @ApiModelProperty("模板类型")
    private TemplateType templateType;


    @Override
    public BooleanBuilder generateQueryBuilder() {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QImportExcelConfig qImportExcelConfig=QImportExcelConfig.importExcelConfig;

        if (Objects.nonNull(this.principalId)) {
            booleanBuilder.and(qImportExcelConfig.principalId.eq(this.principalId));
        }

        if (Objects.nonNull(this.templateDataName)) {
            booleanBuilder.and(qImportExcelConfig.name.contains(this.templateDataName));
        }

        if(Objects.nonNull(this.templateType)){
            booleanBuilder.and(qImportExcelConfig.templateType.eq(this.templateType));
        }
        return booleanBuilder;
    }


}

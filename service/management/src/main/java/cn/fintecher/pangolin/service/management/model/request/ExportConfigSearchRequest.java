package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.enums.ExportType;
import cn.fintecher.pangolin.common.web.MongoSearchRequest;
import cn.fintecher.pangolin.entity.managentment.QExportConfig;
import cn.fintecher.pangolin.entity.managentment.QImportExcelConfig;
import com.querydsl.core.BooleanBuilder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

@Data
public class ExportConfigSearchRequest extends MongoSearchRequest {
    @ApiModelProperty(notes = "委托方ID")
    private String principalId;

    @ApiModelProperty(notes = "模板名称")
    private String templateDataName;

    @ApiModelProperty("类型")
    private ExportType exportType;


    @Override
    public BooleanBuilder generateQueryBuilder() {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QExportConfig qExportConfig=QExportConfig.exportConfig;

        if (Objects.nonNull(this.principalId)) {
            booleanBuilder.and(qExportConfig.principalId.eq(this.principalId));
        }

        if (Objects.nonNull(this.templateDataName)) {
            booleanBuilder.and(qExportConfig.name.contains(this.templateDataName));
        }
        if(Objects.nonNull(this.exportType)){
            booleanBuilder.and(qExportConfig.exportType.eq(this.exportType));
        }

        return booleanBuilder;
    }


}

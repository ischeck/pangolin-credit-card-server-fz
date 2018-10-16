package cn.fintecher.pangolin.service.dataimp.model.request;

import cn.fintecher.pangolin.common.enums.ImportDataExcelStatus;
import cn.fintecher.pangolin.common.enums.TemplateType;
import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * @Author:peishouwen
 * @Desc: 案件导入结果查询
 * @Date:Create in 11:37 2018/7/29
 */
@Data
public class ImportDataExcelRecordRequest extends SearchRequest {

    @ApiModelProperty(notes = "委托方")
    private String principalName;

    @ApiModelProperty(notes = "批次号")
    private String batchNumber;

    @ApiModelProperty("数据导入状态")
    private ImportDataExcelStatus importDataExcelStatus;


    @Override
    public QueryBuilder generateQueryBuilder() {
        BoolQueryBuilder queryBuilder= QueryBuilders.boolQuery();
        if(!StringUtils.isBlank(principalName)){
            queryBuilder.must(wildcardQuery("principalName.keyword","*"+principalName+"*"));
        }
        if(!StringUtils.isBlank(batchNumber)){
            queryBuilder.must(matchPhraseQuery("batchNumber",batchNumber));
        }
        if(Objects.nonNull(importDataExcelStatus)){
            queryBuilder.must(matchPhraseQuery("importDataExcelStatus",importDataExcelStatus.name()));
        }
        return queryBuilder;
    }
}

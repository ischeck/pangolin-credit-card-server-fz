package cn.fintecher.pangolin.service.domain.model.request;

import cn.fintecher.pangolin.common.enums.AssistFlag;
import cn.fintecher.pangolin.common.enums.AssistStatus;
import cn.fintecher.pangolin.common.web.SearchRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Date;
import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

/**
 * @Author : huyanmin
 * @Description : 案件协催申请请求model
 * @Date : 2018/7/16.
 */
@Data
public class AssistCaseApplyRequest extends SearchRequest  {

    @ApiModelProperty("案件Id")
    private String caseId;

    @ApiModelProperty("催收对象名称")
    private String personalName;

    @ApiModelProperty("地址Id")
    private String personalAddressId;

    @ApiModelProperty("联系电话Id")
    private String personalContactId;

    @ApiModelProperty("协助审批部门ID")
    private String assistDeptId;

    @ApiModelProperty("申请原因")
    private String applyReason;

    @ApiModelProperty("协助类型")
    private AssistFlag assistFlag;

    @ApiModelProperty("外访时间")
    private Date applyTime;

    @ApiModelProperty("协催地址")
    private String addressDetail;

    @ApiModelProperty("地址类型")
    private String addressType;

    @ApiModelProperty("关系")
    private String relationShip;

    @ApiModelProperty("电话号码")
    private String assistMobile;

    @ApiModelProperty("信函模板")
    private String letterTemp;

    @Override
    public QueryBuilder generateQueryBuilder(){
        BoolQueryBuilder qb = QueryBuilders
                .boolQuery();
        if (Objects.nonNull(this.caseId)) {
            qb.must(matchPhraseQuery("caseId", this.caseId)).mustNot(matchPhraseQuery("assistStatus", AssistStatus.ASSIST_COMPLETED.toString()));
        }
        return qb;
    }

}

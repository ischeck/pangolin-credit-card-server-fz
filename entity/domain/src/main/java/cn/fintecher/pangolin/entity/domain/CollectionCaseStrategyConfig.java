package cn.fintecher.pangolin.entity.domain;

import cn.fintecher.pangolin.common.enums.StrategyState;
import cn.fintecher.pangolin.common.enums.StrategyType;
import cn.fintecher.pangolin.entity.managentment.Organization;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

import static org.springframework.data.elasticsearch.annotations.FieldType.text;

/**
 * 案件分配策略配置
 * Created by ChenChang on 2018/1/2.
 */
@Data
@Document(indexName = "strategy_config", type = "strategy_config", shards = 1, replicas = 0)
@ApiModel(value = "CollectionCaseStrategyConfig", description = "案件分配策略配置")
public class CollectionCaseStrategyConfig {
    @Id
    private String id;
    @ApiModelProperty("策略名称")
    @Field(type = text, store = true, fielddata = true)
    private String name;
    @ApiModelProperty("公式JSON")
    private String formulaJson;
    @ApiModelProperty("公式")
    private String formula;
    @ApiModelProperty("创建日期")
    @Field(type = FieldType.Date)
    private Date createTime;
    @ApiModelProperty("创建人")
    private String operatorName;
    @ApiModelProperty("对应分配机构")
    private Organization organization;
    @ApiModelProperty("策略类型")
    private StrategyType strategyType;
    @ApiModelProperty("优先级")
    @Field(type = FieldType.Integer)
    private Integer priority;
    private StrategyState strategyState;

    /**
     * 解析公式
     *
     * @param jsonObject
     * @param stringBuilder
     * @return
     * @throws JSONException
     */
    public void analysisFormula(String jsonObject, StringBuilder stringBuilder) throws JSONException {
        JSONArray jsonArray = new JSONArray(jsonObject);
        int iSize = jsonArray.length();
        for (int i = 0; i < iSize; i++) {
            JSONObject jsonObj = jsonArray.getJSONObject(i);
            if (jsonObj.getBoolean("leaf")) {
                stringBuilder.append(jsonObj.get("relation"));
                stringBuilder.append("baseCase." + jsonObj.get("variable"));
                stringBuilder.append(jsonObj.get("symbol"));
                stringBuilder.append("\"");
                stringBuilder.append(jsonObj.get("value"));
                stringBuilder.append("\"");
            } else {
                stringBuilder.append(jsonObj.get("relation"));
                stringBuilder.append("(");
                analysisFormula(jsonObj.getJSONArray("children").toString(), stringBuilder);
                stringBuilder.append(")");
            }
        }
        this.formula = stringBuilder.toString();
    }
}

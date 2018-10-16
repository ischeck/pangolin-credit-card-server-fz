package cn.fintecher.pangolin.common.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author:huyanmin
 * @Desc: 催记字段字段属性
 * @Date:Create 2018/8/22
 */
@Data
public class CaseFollowRecordMatchResponse implements Serializable{

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("PID")
    private String pid;

    @ApiModelProperty("属性名")
    private String attribute;

    @ApiModelProperty("中文名")
    private String name;

    @ApiModelProperty("属性类型")
    private String propertyType;

    @ApiModelProperty("是否必输")
    private String isMustInput;

    @ApiModelProperty("子类")
    private List<CaseFollowRecordMatchResponse> childList;

    @ApiModelProperty("关联list(如承诺还款)")
    private List<String> relationList;
}

package cn.fintecher.pangolin.service.management.model.response;

import cn.fintecher.pangolin.entity.managentment.ContactResult;
import cn.fintecher.pangolin.entity.managentment.CustConfig;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by BBG on 2018/8/4.
 */
@Data
public class CollConfigModel {
    @ApiModelProperty("联络结果")
    List<ContactResult> contactResults;

    @ApiModelProperty("联系对象")
    List<ContactResult> relations;

    @ApiModelProperty("电话状态")
    List<ContactResult> phoneState;

    @ApiModelProperty("电话类型")
    List<ContactResult> phoneType;

    @ApiModelProperty("案件状态")
    List<CustConfig> caseStates;

    @ApiModelProperty("催收状态")
    List<CustConfig> collStates;
}

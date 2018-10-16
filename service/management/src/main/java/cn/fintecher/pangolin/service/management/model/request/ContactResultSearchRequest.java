package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.enums.ConfigState;
import cn.fintecher.pangolin.common.utils.ZWStringUtils;
import cn.fintecher.pangolin.common.web.MongoSearchRequest;
import cn.fintecher.pangolin.entity.managentment.QContactResult;
import com.querydsl.core.BooleanBuilder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * Created by BBG on 2018/8/3.
 */
@Data
public class ContactResultSearchRequest extends MongoSearchRequest {

    @ApiModelProperty(notes = "委托方ID")
    private String principalId;

    @ApiModelProperty(notes = "PID")
    private String pid;

    @ApiModelProperty(notes = "名称")
    private String name;

    @Override
    public BooleanBuilder generateQueryBuilder() {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QContactResult qContactResult = QContactResult.contactResult;
        if(ZWStringUtils.isNotEmpty(this.principalId)){
            booleanBuilder.and(qContactResult.principalId.eq(this.principalId));
        }
        if(ZWStringUtils.isNotEmpty(this.pid)){
            booleanBuilder.and(qContactResult.pid.eq(this.pid));
        }
        if(ZWStringUtils.isNotEmpty(this.name)){
            booleanBuilder.and(qContactResult.name.contains(this.name));
        }
        booleanBuilder.and(qContactResult.configState.eq(ConfigState.ENABLED));
        return booleanBuilder;
    }

}

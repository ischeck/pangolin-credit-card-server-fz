package cn.fintecher.pangolin.service.management.model.request;

import cn.fintecher.pangolin.common.utils.ZWStringUtils;
import cn.fintecher.pangolin.common.web.MongoSearchRequest;
import cn.fintecher.pangolin.entity.managentment.QClockRecord;
import com.querydsl.core.BooleanBuilder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class ClockRecordSearchRequest extends MongoSearchRequest {
    @ApiModelProperty("用户ID")
    private String operator;

    @ApiModelProperty("用户姓名")
    private String operatorName;

    @ApiModelProperty("时间(开始)")
    private String dateStart;

    @ApiModelProperty("时间(开始)")
    private String dateEnd;

    @Override
    public BooleanBuilder generateQueryBuilder() {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QClockRecord qClockRecord = QClockRecord.clockRecord;
        if (ZWStringUtils.isNotEmpty(this.operator)) {
            booleanBuilder.and(qClockRecord.operator.eq(this.operator));
        }
        if (ZWStringUtils.isNotEmpty(this.operatorName)) {
            booleanBuilder.and(qClockRecord.operatorName.contains(this.operatorName));
        }
        if(ZWStringUtils.isNotEmpty(this.dateStart)){
            booleanBuilder.and(qClockRecord.date.goe(this.dateStart));
        }
        if(ZWStringUtils.isNotEmpty(this.dateEnd)){
            booleanBuilder.and(qClockRecord.date.loe(this.dateEnd));
        }
        return booleanBuilder;
    }
}

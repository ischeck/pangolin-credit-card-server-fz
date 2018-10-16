package cn.fintecher.pangolin.service.repair.model.response;

import cn.fintecher.pangolin.entity.repair.*;
import cn.fintecher.pangolin.service.repair.model.RelationShipDataModel;
import cn.fintecher.pangolin.service.repair.model.SpecialTransferDataModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author : hanwannan
 * @Description : 新增支付宝信息
 * @Date : 2018/8/31.
 */
@Data
public class ArchiveDataResponse {

    @ApiModelProperty(notes = "户籍资料")
    private KosekiData kosekiData;
    
    @ApiModelProperty(notes = "户籍备注")
    private List<KosekiRemark> kosekiRemarkList;

    @ApiModelProperty("社保资料")
    private SocialSecurityData socialSecurityData;

    @ApiModelProperty("特调资料")
    private SpecialTransferDataModel specialTransferDataModel;

    @ApiModelProperty("计生资料")
    private SpecialTransferDataModel familyPlanningDataModel;

    @ApiModelProperty("关联关系")
    private List<RelationShipDataModel> relationshipList;
}


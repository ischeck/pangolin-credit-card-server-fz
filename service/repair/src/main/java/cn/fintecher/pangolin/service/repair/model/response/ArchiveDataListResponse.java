package cn.fintecher.pangolin.service.repair.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author : hanwannan
 * @Description : 新增支付宝信息
 * @Date : 2018/8/31.
 */
@Data
public class ArchiveDataListResponse {

    @ApiModelProperty(notes = "途径")
    private String channel;
    
    @ApiModelProperty(notes = "姓名")
    private String name;

    @ApiModelProperty("证件号")
    private String idNo;

    @ApiModelProperty("导入日期")
    private Date importDate;

    @ApiModelProperty("详细资料")
    private String detailData;

}

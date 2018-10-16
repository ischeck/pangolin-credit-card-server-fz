package cn.fintecher.pangolin.service.domain.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 9:08 2018/9/12
 */
@Data
public class CaseCommonProResponse {

    @ApiModelProperty("城市数据")
    private Set<String> citys=new HashSet<>();

    @ApiModelProperty("催收员")
    private Set<String> collectors=new HashSet<>();

    @ApiModelProperty("案件所属部门")
    private Set<String> departs=new HashSet<>();
}

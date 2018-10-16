package cn.fintecher.pangolin.common.model;

import lombok.Data;
import java.util.List;
import java.util.Set;

@Data
public class OperatorSearchModel{

    private Set<String> roleIds;

    private Set<String> organizationIds;

}

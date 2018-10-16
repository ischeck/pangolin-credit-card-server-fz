package cn.fintecher.pangolin.common.enums;

/**
 * @Author:huyanmin
 * @Desc: 行动代码相关的字段
 * @Date:Create in 17:23 2018/8/15
 */
public enum RelationList{

    //承诺还款
    PROMISED_PAYMENT("承诺还款"),
    //承诺部分还款
    PROMISED_SUB_PAYMENT("承诺部分还款"),
    //持卡人承诺还款
    HOLDER_PROMISED__PAYMENT("持卡人承诺还款"),
    //PTP
    PTP("PTP"),
    //CP
    CP("CP"),
    //已还款
    HAS_PAYMENT("已还款"),
    //承诺金额
    PROMISED_AMOUNT("承诺金额"),
    //承诺日期
    PROMISED_DATE("承诺日期"),
    //已还款金额
    HAS_AMOUNT("已还款金额"),
    //已还款日期
    HAS_DATE("已还款日期");


    private String remark;

    RelationList(String remark) {
        this.remark = remark;
    }

    public String getRemark() {
        return remark;
    }
}

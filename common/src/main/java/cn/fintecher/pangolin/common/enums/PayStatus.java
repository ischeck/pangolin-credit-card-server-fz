package cn.fintecher.pangolin.common.enums;

/**
 * @Author:peishouwen
 * @Desc: 还款状态
 * @Date:Create in 13:58 2018/7/19
 */
public enum PayStatus implements EnumMessage {
    //未还款
    UN_PAY,
    //部分还款
    PARTIAL_PAY,
    //已还款
    HAS_PAY,
    //无法还款
    UNABLE_PAY

}

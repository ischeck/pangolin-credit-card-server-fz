package cn.fintecher.pangolin.common.enums;

/**
 * Created by huyanmin on 2018/7/18.
 */
public enum AssistApprovedResult implements EnumMessage {

    //本地审批拒绝
    LOCAL_REJECT,
    //本地审批通过
    LOCAL_PASS,
    //异地审批拒绝
    ASSIST_REJECT,
    //异地审批通过
    ASSIST_PASS
}

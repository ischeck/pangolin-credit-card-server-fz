package cn.fintecher.pangolin.common.enums;

/**
 * Created by huyanmin on 2018/7/17.
 */
public enum AssistApprovedStatus implements EnumMessage {

    //本地待审批
    LOCAL_WAIT_APPROVAL,
    //本地审批完成
    LOCAL_COMPLETED,
    //异地待审批
    ASSIST_WAIT_APPROVAL,
    //异地审批完成
    ASSIST_COMPLETED
}

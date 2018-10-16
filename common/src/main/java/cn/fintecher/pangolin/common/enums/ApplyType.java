package cn.fintecher.pangolin.common.enums;

/**
 * Created by huyanmin on 2018/7/19.
 */
public enum ApplyType implements EnumMessage {

    //协催审批流程
    LOCAL_ASSIST_APPLY,
    //异地协催审批流程
    DIFFERENT_ASSIST_APPLY,
    //减免申请
    DERATE_APPLY,
    //补款申请
    SUPPLEMENT_APPLY,
    //报案申请
    REPORT_CASE_APPLY,
    //公共案件申请
    PUBLIC_CASE_APPLY,
    //公共案件分配申请
    PUBLIC_DISTRIBUTE_CASE_APPLY,
    //查账申请
    CHECK_OVERDUE_AMOUNT_APPLY,
    //资料复核
    CHECK_MATERIAL_APPLY,
    //留案申请
    LEAVE_CASE_APPLY

}

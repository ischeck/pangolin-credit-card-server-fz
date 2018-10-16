package cn.fintecher.pangolin.common.enums;

/**
 * Created by huyanmin on 2018/7/4.
 */
public enum TemplateType implements EnumMessage {
    //案件导入
    IMPORT_CASE,
    //案件更新导入
    IMPORT_UPDATE_CASE,
    //委前催记导入
    IMPORT_FOLLOW_RECORD,
    //停催案件导入
    IMPORT_END_CASE,
    //留案案件导入
    IMPORT_LEFT_CASE,
    //对账单导入
    IMPORT_BILL,
    //调整城市导入
    IMPORT_CHANGE_CITY,
    //警告信息导入
    IMPORT_WARNING_INFO,
    //工单导入
    IMPORT_WORKER_ORDER
}

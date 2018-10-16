package cn.fintecher.pangolin.common.enums;

/**
 * @Author:peishouwen
 * @Desc: 消息类型
 * @Date:Create in 10:15 2018/9/4
 */
public enum MessageType implements EnumMessage {
    //案件导入
    IMPORT_EXCEL_MSG,
    //导入确认
    IMPORT_CONFIRMED_MSG,
    //申请审批
    APPLY_APPROVE_MSG,
    //协催撤回
    ASSIST_CALL_BACK,
    //结束协催
    STOP_ASSIST,
    //案件更新导入
    IMPORT_UPDATE_CASE,
   //案件更新确认
   IMPORT_UPDATE_CONFIRMED,
   //警告信息
   IMPORT_WARNING_INFO,
   //警告信息确认
   IMPORT_WARNING_CONFIRMED,
   //工单信息
   IMPORT_WORKER_ORDER,
   //工单信息确认
   IMPORT_WORKER_CONFIRMED,
   //对账单
   IMPORT_BILL,
   //对账单确认
   IMPORT_BILL_CONFIRMED,
   //留案导入
   IMPORT_LEFT_CASE ,
   //留案确认
   IMPORT_LEFT_CONFIRMED ,
   // 停催
   IMPORT_END_CASE,
    // 停催确认
    IMPORT_END_CONFIRMED,
    //委前催计导入
    IMPORT_FOLLOW_RECORD,
    //委前催计确认
    IMPORT_FOLLOW_CONFIRMED,
    //城市调整导入
    IMPORT_CHANGE_CITY,
    //城市调整确认
    IMPORT_CHANGE_CONFIRMED,
    //备忘录
    COMMENT,
    //策略分配
    DISTRIBUTE_CASE
}

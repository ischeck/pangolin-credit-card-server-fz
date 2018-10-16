package cn.fintecher.pangolin.common.enums;

/**
 * @Author:peishouwen
 * @Desc: 案件导入状态
 * @Date:Create in 15:07 2018/7/28
 */
public enum ImportDataExcelStatus implements EnumMessage {
    //正在导入
    IMPORTING,
    //导入失败
    IMPORT_FAILED,
    //导入成功
    IMPORT_SUCCESSFULLY,
    //已取消
    IMPORT_CANCEL,
    //导入确认中
    IMPORT_CONFIRMING,
    //确认失败
    UN_CONFIRMED,
    //已确认
    CONFIRMED;

}

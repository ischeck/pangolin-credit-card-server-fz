package cn.fintecher.pangolin.common.enums;

/**
 * @Author:peishouwen
 * @Desc: 案件数据状态
 * @Date:Create in 17:52 2018/7/20
 */
public enum CaseDataStatus implements EnumMessage {
    //在案
    IN_POOL,
    //停催
    PAUSE,
    //结清
    SETTLT,
    //退案
    OUT_POOL,
    //删除
    DELETE,
    //手工删除
    DELETE_MANUAL

}

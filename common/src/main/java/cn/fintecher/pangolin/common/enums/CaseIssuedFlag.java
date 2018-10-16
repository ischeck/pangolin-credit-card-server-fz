package cn.fintecher.pangolin.common.enums;

/**
 * @Author:peishouwen
 * @Desc: 案件下发标识
 * @Date:Create in 11:10 2018/7/20
 */
public enum CaseIssuedFlag implements EnumMessage{
    //地区待分配
    AREA_UN_DIS,
    //地区未下发
    AREA_UN_ISSUED,
    //地区已下发
    AREA_HAS_ISSUED,
    //个人未下发
    PERSONAL_UN_ISSUED,
    //个人已下发
    PERSONAL_HAS_ISSUED;
}

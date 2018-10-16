package cn.fintecher.pangolin.common.enums;


public enum CaseFiller implements EnumMessage {
    //今日跟催
    TODAY_FOLLOW,
    //明日跟催
    TOMORROW_FOLLOW,
    //PTP
    PTP,
    //重点跟进
    MAJOR_FOLLOW,
    //1-3天未跟
    ONE_TO_THREE_NO_FOLLOW,
    //3-6天未跟
    FOUR_TO_SIX_NO_FOLLOW,
    //3天内退案
    THREE_DAYS_LEFT,
    //7天内退案
    SEVEN_DAYS_LEFT;
}

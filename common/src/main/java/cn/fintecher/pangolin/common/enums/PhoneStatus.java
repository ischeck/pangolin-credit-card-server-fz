package cn.fintecher.pangolin.common.enums;

/**
 * Created by ChenChang on 2018/6/27.
 */
public enum PhoneStatus implements EnumMessage {
    //正常
    NORMAL,
    //空号
    VACANT_NUMBER,
    //停机
    HALT,
    //关机
    POWER_OFF,
    //未接
    UNANSWERED,
    //忙音
    BUSY_TONE,
    //短信呼
    MSG_CALL,
    //易主
    CHANGE_OWNER,
    //未知
    UNKNOWN {
        @Override
        public String getMessageKey(Enum<?> e) {
            return "common.unknown";
        }
    }


}

package cn.fintecher.pangolin.common.enums;

/**
 * @Author:胡艳敏
 * @Desc: 协助标识
 * @Date:Create in 17:23 2018/7/17
 */
public enum AssistFlag implements EnumMessage {
    //协助
    HAS_ASSIST,
    //未协助
    NO_ASSIST,
    //异地外访协助
    OFFSITE_OUT_ASSIST,
    //本地外访协助
    LOCAL_OUT_ASSIST,
    //异地电话协助
    OFFSITE_PHONE_ASSIST,
    //信函
    LETTER_ASSIST
}

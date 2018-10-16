package cn.fintecher.pangolin.common.enums;

/**
 * Created by ChenChang on 2018/6/27.
 */
public enum Marital implements EnumMessage {
    //未婚
    UNMARRIED,
    //已婚
    MARRIED,
    //未知
    UNKNOWN {
        @Override
        public String getMessageKey(Enum<?> e) {
            return "common.unknown";
        }
    }

}
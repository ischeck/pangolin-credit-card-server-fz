package cn.fintecher.pangolin.common.enums;

public enum Sex implements EnumMessage {
    //男
    MALE,
    //女
    FEMALE,
    //未知
    UNKNOWN {
        @Override
        public String getMessageKey(Enum<?> e) {
            return "common.unknown";
        }
    }
}

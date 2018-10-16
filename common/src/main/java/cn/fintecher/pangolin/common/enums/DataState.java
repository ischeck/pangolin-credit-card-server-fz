package cn.fintecher.pangolin.common.enums;

/**
 * @Author:peishouwen
 * @Desc: 电话状态
 * @Date:Create in 15:37 2018/8/1
 */
public enum DataState implements EnumMessage {
    //未知
    UNKNOWN{
        @Override
        public String getMessageKey(Enum<?> e) {
            return "common.unknown";
        }
    }
}


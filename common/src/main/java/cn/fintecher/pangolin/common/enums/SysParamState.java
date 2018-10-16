package cn.fintecher.pangolin.common.enums;

/**
 * Created by ChenChang on 2018/7/2.
 */
public enum SysParamState implements EnumMessage {
    START {
        @Override
        public String getMessageKey(Enum<?> e) {
            return "common.enable";
        }
    },//启用"
    STOP {
        @Override
        public String getMessageKey(Enum<?> e) {
            return "common.disabled";
        }
    }//停用"

}

package cn.fintecher.pangolin.common.enums;

/**
 * Created by ChenChang on 2018/7/2.
 */
public enum ComplianceState implements EnumMessage {

    ENABLED {
        @Override
        public String getMessageKey(Enum<?> e) {
            return "common.enable";
        }
    }, DISABLED {
        @Override
        public String getMessageKey(Enum<?> e) {
            return "common.disabled";
        }
    }

}

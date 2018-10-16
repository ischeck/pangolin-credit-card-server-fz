package cn.fintecher.pangolin.common.enums;

/**
 * Created by huyanmin on 2018/8/24.
 */
public enum ConfigState implements EnumMessage {

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

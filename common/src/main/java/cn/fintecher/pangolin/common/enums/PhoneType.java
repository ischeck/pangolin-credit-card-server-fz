package cn.fintecher.pangolin.common.enums;

/**
 * Created by huyanmin on 2018/7/6.
 */
public enum PhoneType implements EnumMessage {
    //手机
    MOBILE,
    //住宅电话
    HOME_PHONE,
    //单位电话
    OFFICE_PHONE,
    //未知
    UNKNOWN {
        @Override
        public String getMessageKey(Enum<?> e) {
            return "common.unknown";
        }
    }
}

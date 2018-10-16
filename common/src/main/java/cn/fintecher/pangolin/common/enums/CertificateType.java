package cn.fintecher.pangolin.common.enums;

/**
 * @Author:peishouwen
 * @Desc: 证件类型
 * @Date:Create in 15:30 2018/7/11
 */
public enum CertificateType implements EnumMessage {
    //身份证
    IDCARD,
    //护照
    PASSPORT,
    //军官证
    MILITARY_OFFICER_CARD,
    //居住证
    RESIDENCE_PERMIT,
    //其他
    OTHER{
        @Override
        public String getMessageKey(Enum<?> e) {
            return "common.other";
        }
    }


}

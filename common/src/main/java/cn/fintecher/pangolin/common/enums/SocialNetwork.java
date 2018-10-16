package cn.fintecher.pangolin.common.enums;

/**
 * Created by ChenChang on 2018/6/27.
 */
public enum SocialNetwork implements EnumMessage {
    //微信
    WECHAT,
    //QQ
    QQ,
    //支付宝
    ALI_PAY,
    //其他
    OTHER {
        @Override
        public String getMessageKey(Enum<?> e) {
            return "common.other";
        }
    }
}

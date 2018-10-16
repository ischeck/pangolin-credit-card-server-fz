package cn.fintecher.pangolin.common.enums;

/**
 * Created by ChenChang on 2018/7/2.
 */
public enum BusinessType implements EnumMessage {
    //信用卡
    CREDIT_CARD,
    //个贷
    PERSONAL_LOAN,
    //车贷
    CAR_LOAN,
    //房贷
    HOUSE_LOAN,
    //其他
    OTHER {
        @Override
        public String getMessageKey(Enum<?> e) {
            return "common.other";
        }
    }
}

package cn.fintecher.pangolin.common.enums;

/**
 * Created by ChenChang on 2018/7/2.
 */
public enum PrincipalType implements EnumMessage {
    //保险公司
    INSURANCE,
    //银行
    BANK,
    //贷款公司
    LOAN_COMPANY,
    //催收机构
    AGENCIES,
    //其他
    OTHER {
        @Override
        public String getMessageKey(Enum<?> e) {
            return "common.other";
        }
    };
}

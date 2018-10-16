package cn.fintecher.pangolin.common.enums;

/**
 * @Author:peishouwen
 * @Desc: 地址信息类型
 * @Date:Create in 17:23 2018/7/11
 */
public enum AddressType implements EnumMessage {
    //住宅地址
    HOUSE_ADDR,
    //单位地址
    COMPANY_ADDR,
    //户籍地址
    NATIVE_ADDR,
    //邮寄地址
    BILL_ADDR,
    //身份证地址
    IDCARD_ADDR,
    //其他地址
    OTHER {
        @Override
        public String getMessageKey(Enum<?> e) {
            return "common.other";
        }
    }
}

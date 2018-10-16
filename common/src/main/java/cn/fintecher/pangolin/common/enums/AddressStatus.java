package cn.fintecher.pangolin.common.enums;


public enum  AddressStatus implements EnumMessage{
    //居住
    VALIDADDR,
    //搬迁
    UNRELATEDADDR,
    //拆迁
    SALEOFF,
    //租房
    RENTOUT,
    //无人
    VACANCY,
    //无法确定
    UNKNOWN {
        @Override
        public String getMessageKey(Enum<?> e) {
            return "common.unknown";
        }
    }
}

package cn.fintecher.pangolin.common.enums;

/**
 * Created by huyanmin on 2018/7/18.
 */
public enum PaymentStatus implements EnumMessage {

    //还款待确认
    WAIT_CONFIRMED,
    //还款确认中
    CONFIRMING,
    //还款已确认
    CONFIRMED
}

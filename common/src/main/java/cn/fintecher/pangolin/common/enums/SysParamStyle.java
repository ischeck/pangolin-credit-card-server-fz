package cn.fintecher.pangolin.common.enums;

/**
 * Created by ChenChang on 2018/7/2.
 */
public enum SysParamStyle implements EnumMessage {
    CIRCULATION,//流转参数
    BATCH,//批量参数
    TEMPLATE,//模版参数
    CALL,//呼叫中心参数
    SMS,//短信参数
    OTHER {
        @Override
        public String getMessageKey(Enum<?> e) {
            return "common.other";
        }
    }//其他参数
}

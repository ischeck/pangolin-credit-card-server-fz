package cn.fintecher.pangolin.common.enums;

/**
 * Created by huyanmin on 2018/7/16.
 */
public enum AssistType implements EnumMessage {

    //异地外访协助
    DIFF_OUT_ASSIST,
    //异地电话协助
    DIFF_PHONE_ASSIST,
    //信函
    LETTER,
    //本地外访协助
    SAME_OUT_ASSIST,
    //其他
    OTHER {
        @Override
        public String getMessageKey(Enum<?> e) {
            return "common.other";
        }
    }
}

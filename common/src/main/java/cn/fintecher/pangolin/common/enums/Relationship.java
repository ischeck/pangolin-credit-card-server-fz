package cn.fintecher.pangolin.common.enums;

/**
 * 人际关系
 * Created by ChenChang on 2018/6/28.
 */
public enum Relationship implements EnumMessage {
    //本人
    SELF,
    //配偶
    SPOUSE,
    //父母
    PARENT,
    //家人
    CHILD,
    //亲属
    RELATIVES,
    //同事
    COLLEUAGUE,
    //朋友
    FRIEND,
    //其他
    OTHER {
        @Override
        public String getMessageKey(Enum<?> e) {
            return "common.other";
        }
    },
    //单位
    OFFICE,
    //同学
    CLASSMATE
}

package cn.fintecher.pangolin.common.enums;

import com.google.common.base.CaseFormat;

/**
 * Created by ChenChang on 2018/6/27.
 */
public interface EnumMessage {
    default String getMessageKey(Enum<?> e) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, e.getClass().getSimpleName()) + '.' + e.name().toLowerCase();
    }
}

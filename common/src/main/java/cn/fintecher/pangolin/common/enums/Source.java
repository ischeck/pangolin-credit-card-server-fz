package cn.fintecher.pangolin.common.enums;

/**
 * @Author:huyanmin
 * @Desc: 数据来源
 * @Date:Create in 2018/8/29
 */
public enum Source implements EnumMessage {
    //导入
    IMPORT,
    //修复
    REPAIRED,
    OTHER {
        @Override
        public String getMessageKey(Enum<?> e) {
            return "common.other";
        }
    }
}

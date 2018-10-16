package cn.fintecher.pangolin.common.utils;

/**
 * @Author:peishouwen
 * @Desc: 系统全局变量
 * @Date:Create in 16:35 2018/9/17
 */
public class Constants {

    /**晚间批量调度时间**/
    public final static String SYSPARAM_OVERNIGHT = "overNight.job.cron";

    /***晚间批量调度状态 0-未执行 1-正在执行*/
    public final static String SYSPARAM_OVERNIGHT_STATUS = "overNight.job.status";

    /**备忘录提醒间隔**/
    public final static String SYSPARAM_COMMENT_REMIND = "commentRemind.job.minute";
}

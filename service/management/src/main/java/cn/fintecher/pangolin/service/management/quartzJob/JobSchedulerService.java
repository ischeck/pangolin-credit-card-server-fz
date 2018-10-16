package cn.fintecher.pangolin.service.management.quartzJob;

import cn.fintecher.pangolin.common.utils.Constants;
import cn.fintecher.pangolin.entity.managentment.SysParam;
import cn.fintecher.pangolin.service.management.repository.SystemParamRepository;
import cn.fintecher.pangolin.service.management.service.SysParamService;
import org.apache.commons.lang.StringUtils;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Author:peishouwen
 * @Desc: 任务调度的
 * @Date:Create in 15:41 2018/9/17
 */
@Service("jobSchedulerService")
public class JobSchedulerService {


    @Autowired
    Scheduler scheduler;

    @Autowired
    SysParamService sysParamService;

    public void buildScheduleJob() throws SchedulerException {
        buildOverNightJob();
        buildCommentRemindJob();
    }

    public void buildOverNightJob() throws SchedulerException {
        //任务名称
        String jobName = "overNightJob";
        //任务所属分组
        String group = OverNightJob.class.getName();
        //查询任务调度时间
        SysParam sysParam = new SysParam();
        sysParam.setCode(Constants.SYSPARAM_OVERNIGHT);
        sysParam = sysParamService.findOne(sysParam);
        if (Objects.nonNull(sysParam)) {
            String cronStr = sysParam.getValue();
            //时间长度必须为6位
            if (StringUtils.isNotBlank(cronStr) && StringUtils.length(cronStr) == 6) {
                String hours = cronStr.substring(0, 2);
                String mis = cronStr.substring(2, 4);
                String second = cronStr.substring(4, 6);
                cronStr = second.concat(" ").concat(mis).concat(" ").concat(hours).concat(" * * ?");
                addScheduleJob(jobName, group, cronStr, OverNightJob.class);
            }
        }
    }


    public void buildCommentRemindJob() throws SchedulerException {
        //任务名称
        String jobName = "CommentRemindJob";
        //任务所属分组
        String group = CommentRemindJob.class.getName();
        //查询任务调度时间
        SysParam sysParam = new SysParam();
        sysParam.setCode(Constants.SYSPARAM_COMMENT_REMIND);
        sysParam = sysParamService.findOne(sysParam);
        if (Objects.nonNull(sysParam)) {
            String minute = sysParam.getValue();
            if (StringUtils.isNotBlank(minute)) {
                String cronStr = "* 0/".concat(minute).concat(" * * * ?");
                addScheduleJob(jobName, group, cronStr, CommentRemindJob.class);
            }
        }
    }

    private void addScheduleJob(String jobName, String group, String cronStr,Class clazz) throws SchedulerException {
        JobDetail jobDetail = JobBuilder
                .newJob(clazz).withIdentity(jobName, group).build();
        if (!scheduler.checkExists(jobDetail.getKey())) {
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronStr);
            //创建任务触发器
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName, group).
                    withSchedule(scheduleBuilder).build();
            //将触发器与任务绑定到调度器内
            scheduler.scheduleJob(jobDetail, trigger);
        }
    }
}

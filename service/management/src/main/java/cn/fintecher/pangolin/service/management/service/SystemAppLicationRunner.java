package cn.fintecher.pangolin.service.management.service;

import cn.fintecher.pangolin.service.management.quartzJob.JobSchedulerService;
import org.apache.juli.logging.LogFactory;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


/**
 * @Author:peishouwen
 * @Desc: 自定义系统系统启动时需要执行操作
 * @Date:Create in 17:36 2018/9/17
 */
@Component
@Order(1)
public class SystemAppLicationRunner implements ApplicationRunner {

   Logger logger= LoggerFactory.getLogger(SystemAppLicationRunner.class);

    @Autowired
    JobSchedulerService jobSchedulerService;
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        startSystemJob();
    }

    /**
     * 启动系统中的任务调度
     * @throws SchedulerException
     */
    public void startSystemJob() throws SchedulerException {
        logger.info("启动系统晚间批量任务");
        jobSchedulerService.buildScheduleJob();
    }


}

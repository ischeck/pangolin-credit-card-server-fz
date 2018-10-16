package cn.fintecher.pangolin.service.management.quartzJob;

import cn.fintecher.pangolin.common.utils.InnerServiceUrl;
import cn.fintecher.pangolin.service.management.service.ClockService;
import org.apache.juli.logging.LogFactory;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @Author:peishouwen
 * @Desc: 系统晚间批量调度
 * @Date:Create in 15:24 2018/9/17
 */
@Component("overNightJob")
public class OverNightJob extends QuartzJobBean {

    Logger logger = LoggerFactory.getLogger(OverNightJob.class);

    @Autowired
    ClockService clockService;

    @Autowired
    RestTemplate restTemplate;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("晚间批量启动");

        new Thread(()->{
            logger.info("打卡记录生成");
            clockService.createClockRecord();
        }).start();

        new Thread(()->{
            logger.info("案件退案转移");
            restTemplate.getForEntity(InnerServiceUrl.DOMIAN_SERVICE_HISCASETRANS,Void.class);
        }).start();

        new Thread(()->{
            logger.info("案件记录处理");
            restTemplate.getForEntity(InnerServiceUrl.DOMIAN_SERVICE_CASERECORDHANDLE,Void.class);
        }).start();

    }
}

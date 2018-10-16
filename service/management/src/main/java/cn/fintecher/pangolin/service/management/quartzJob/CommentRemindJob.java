package cn.fintecher.pangolin.service.management.quartzJob;

import cn.fintecher.pangolin.common.utils.Constants;
import cn.fintecher.pangolin.common.utils.InnerServiceUrl;
import cn.fintecher.pangolin.entity.managentment.SysParam;
import cn.fintecher.pangolin.service.management.service.SysParamService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Component("commentRemindJob")
public class CommentRemindJob extends QuartzJobBean {

    Logger logger = LoggerFactory.getLogger(OverNightJob.class);

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    SysParamService sysParamService;


    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        new Thread(() -> {
            logger.info("备忘录提醒");
            SysParam sysParam = new SysParam();
            sysParam.setCode(Constants.SYSPARAM_COMMENT_REMIND);
            sysParam = sysParamService.findOne(sysParam);
            if (Objects.nonNull(sysParam)) {
                String minute = sysParam.getValue();
                restTemplate.getForEntity(InnerServiceUrl.DATAIMP_SERVICE_COMMENTREMIND +minute, Void.class);

            }}).start();
        }
    }

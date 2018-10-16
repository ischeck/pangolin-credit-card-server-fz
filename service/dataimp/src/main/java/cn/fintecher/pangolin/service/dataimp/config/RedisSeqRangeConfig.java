package cn.fintecher.pangolin.service.dataimp.config;

import com.xuanner.seq.range.impl.redis.RedisSeqRangeMgr;
import com.xuanner.seq.sequence.impl.DefaultRangeSequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 增加一个Redis自增序列号生成器配置
 * Created by ChenChang on 2018/6/26.
 */
@Configuration
public class RedisSeqRangeConfig {
    @Autowired
    private RedisProperties redisProperties;

    /**
     * 这里没有单独增加序列配置，直接使用了系统的
     * 可以考虑增加单独的redis为序列号服务
     *
     * @return
     */
    @Bean
    public RedisSeqRangeMgr redisSeqRangeMgr() {
        RedisSeqRangeMgr redisSeqRangeMgr = new RedisSeqRangeMgr();
        redisSeqRangeMgr.setIp(redisProperties.getHost());//IP[必选]
        redisSeqRangeMgr.setPort(redisProperties.getPort());//PORT[必选]
        redisSeqRangeMgr.setAuth(redisProperties.getPassword());//密码[可选]看你的redis服务端配置是否需要密码
        redisSeqRangeMgr.setStep(1);//每次取数步长[可选] 默认：1000 步长越大效率越高
        redisSeqRangeMgr.init();
        return redisSeqRangeMgr;
    }

    /**
     * 举例 如之前使用的caseSeq
     * 这里可以定义通用的序列 使用Name区分不同的序列
     * 当然可以直接new个序列直接使用 使用相同的name即可递增序列 如委托方不同
     *
     * @param redisSeqRangeMgr
     * @return
     */
    @Bean
    public DefaultRangeSequence batchNumberSeq(RedisSeqRangeMgr redisSeqRangeMgr) {
        DefaultRangeSequence batchNumberSeq = new DefaultRangeSequence();
        batchNumberSeq.setName("batchNumber");//使用Name区分不同的序列
        batchNumberSeq.setSeqRangeMgr(redisSeqRangeMgr);
        return batchNumberSeq;
    }
}

package cn.fintecher.pangolin.service.dataimp;

import com.xuanner.seq.range.impl.redis.RedisSeqRangeMgr;
import com.xuanner.seq.sequence.impl.DefaultRangeSequence;

/**
 * Created by ChenChang on 2018/6/26.
 */
public class SeqTest {

    public static void main(String[] args) {
        //利用Redis获取区间管理器
        RedisSeqRangeMgr redisSeqRangeMgr = new RedisSeqRangeMgr();
        redisSeqRangeMgr.setIp("192.168.3.2");//IP[必选]
        redisSeqRangeMgr.setPort(46379);//PORT[必选]
//        redisSeqRangeMgr.setAuth("xxx");//密码[可选]看你的redis服务端配置是否需要密码
        redisSeqRangeMgr.setStep(1);//每次取数步长[可选] 默认：1000

        redisSeqRangeMgr.init();
        //构建序列号生成器
        DefaultRangeSequence defaultRangeSequence = new DefaultRangeSequence();
        defaultRangeSequence.setName("user");
        defaultRangeSequence.setSeqRangeMgr(redisSeqRangeMgr);

        long start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            System.out.println("++++++++++id:" + defaultRangeSequence.nextValue());
        }
        System.out.println("interval time:" + (System.currentTimeMillis() - start));
        DefaultRangeSequence defaultRangeSequence1 = new DefaultRangeSequence();
        defaultRangeSequence1.setName("user");
        defaultRangeSequence1.setSeqRangeMgr(redisSeqRangeMgr);
        for (int i = 0; i < 100; i++) {
            System.out.println("++++++++++id1:" + defaultRangeSequence.nextValue());
        }
    }
}

package cn.fintecher.pangolin.service.dataimp.service;

import com.xuanner.seq.sequence.impl.DefaultRangeSequence;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @Author:peishouwen
 * @Desc: 批次号生成规则
 * @Date:Create in 13:43 2018/7/26
 */
@Service("batchNumberSeqService")
public class BatchNumberSeqService {
    @Autowired
    DefaultRangeSequence batchNumberSeq;

    /**
     * 获取批次号
     *
     * @return
     * @throws Exception
     */
    public String getBatchNumberSeq() {
        long seqNum = batchNumberSeq.nextValue();
        String nowDate = LocalDate.now().toString("yyyyMMdd");
        return nowDate.concat(String.valueOf(seqNum));
    }
}

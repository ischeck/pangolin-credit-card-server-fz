package cn.fintecher.pangolin.service.dataimp.repository;

import cn.fintecher.pangolin.entity.domain.PayAmtLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 15:44 2018/8/8
 */
public interface ImpPayAmtLogRepository extends ElasticsearchRepository<PayAmtLog, String> {
}



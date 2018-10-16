package cn.fintecher.pangolin.service.dataimp.repository;

import cn.fintecher.pangolin.entity.domain.CaseTransferLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 14:49 2018/9/11
 */
public interface CaseTransferLogRepository extends ElasticsearchRepository<CaseTransferLog,String> {
}

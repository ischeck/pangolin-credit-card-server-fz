package cn.fintecher.pangolin.service.domain.respository;

import cn.fintecher.pangolin.entity.domain.CaseOperatorLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CaseOperatorLogRepository extends ElasticsearchRepository<CaseOperatorLog,String> {

}
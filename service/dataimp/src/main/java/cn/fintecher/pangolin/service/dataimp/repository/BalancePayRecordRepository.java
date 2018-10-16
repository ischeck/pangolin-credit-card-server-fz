package cn.fintecher.pangolin.service.dataimp.repository;

import cn.fintecher.pangolin.entity.domain.BalancePayRecord;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BalancePayRecordRepository extends ElasticsearchRepository<BalancePayRecord,String> {
}

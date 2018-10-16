package cn.fintecher.pangolin.service.dataimp.repository;

import cn.fintecher.pangolin.entity.domain.PaymentRecord;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ImpPaymentRecordModelRepository extends ElasticsearchRepository<PaymentRecord, String> {
}

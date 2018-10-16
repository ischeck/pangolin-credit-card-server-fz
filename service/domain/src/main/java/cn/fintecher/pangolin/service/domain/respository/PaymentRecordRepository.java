package cn.fintecher.pangolin.service.domain.respository;


import cn.fintecher.pangolin.entity.domain.PaymentRecord;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by huyanmin on 2018/7/18.
 */
public interface PaymentRecordRepository extends ElasticsearchRepository<PaymentRecord, String> {

}

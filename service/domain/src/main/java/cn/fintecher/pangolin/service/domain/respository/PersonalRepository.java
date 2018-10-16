package cn.fintecher.pangolin.service.domain.respository;


import cn.fintecher.pangolin.entity.domain.Personal;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by ChenChang on 2017/8/14.
 */
public interface PersonalRepository extends ElasticsearchRepository<Personal, String> {

    /**
     * 证件号查询客户信息
     * @param certificateNo
     * @return
     */
    Personal findAllByCertificateNo(String certificateNo);
}

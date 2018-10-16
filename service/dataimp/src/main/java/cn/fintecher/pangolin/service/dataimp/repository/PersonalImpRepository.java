package cn.fintecher.pangolin.service.dataimp.repository;


import cn.fintecher.pangolin.entity.domain.Personal;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * Created by ChenChang on 2017/8/14.
 */
public interface PersonalImpRepository extends ElasticsearchRepository<Personal, String> {

    /**
     * 证件号查询客户信息
     * @param certificateNo
     * @return
     */
    Personal findByCertificateNo(String certificateNo);
}

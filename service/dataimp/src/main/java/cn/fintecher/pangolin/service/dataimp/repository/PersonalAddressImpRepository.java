package cn.fintecher.pangolin.service.dataimp.repository;


import cn.fintecher.pangolin.entity.domain.PersonalAddress;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by huyanmin on 2018/7/30.
 */
public interface PersonalAddressImpRepository extends ElasticsearchRepository<PersonalAddress, String> {
}

package cn.fintecher.pangolin.service.domain.respository;


import cn.fintecher.pangolin.entity.domain.PersonalContact;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * Created by huyanmin on 2018/7/30.
 */
public interface PersonalContactRepository extends ElasticsearchRepository<PersonalContact, String> {

}

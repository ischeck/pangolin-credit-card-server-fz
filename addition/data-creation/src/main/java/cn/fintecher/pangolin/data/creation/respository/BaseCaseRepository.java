package cn.fintecher.pangolin.data.creation.respository;


import cn.fintecher.pangolin.entity.domain.BaseCase;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by ChenChang on 2017/8/14.
 */
public interface BaseCaseRepository extends ElasticsearchRepository<BaseCase, String> {
}

package cn.fintecher.pangolin.service.dataimp.repository;


import cn.fintecher.pangolin.entity.domain.CollectionCaseStrategyConfig;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by ChenChang on 2018/8/8.
 */
public interface CollectionCaseStrategyConfigRepository extends ElasticsearchRepository<CollectionCaseStrategyConfig, String> {
}

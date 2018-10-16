package cn.fintecher.pangolin.service.dataimp.repository;


import cn.fintecher.pangolin.entity.domain.AssistCollectionCase;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by huyanmin on 2018/7/16.
 */
public interface AssistCaseRepository extends ElasticsearchRepository<AssistCollectionCase, String> {
}

package cn.fintecher.pangolin.service.dataimp.repository;


import cn.fintecher.pangolin.entity.domain.BaseCase;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by huyanmin on 2018/7/26.
 */
public interface ImportBaseCaseRepository extends ElasticsearchRepository<BaseCase, String> {
}

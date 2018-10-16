package cn.fintecher.pangolin.service.dataimp.repository;


import cn.fintecher.pangolin.entity.domain.AssistCollectionCase;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**外访协助
 * Created by huyanmin on 2018/7/16.
 */
public interface ImpAssistCaseRepository extends ElasticsearchRepository<AssistCollectionCase, String> {
}

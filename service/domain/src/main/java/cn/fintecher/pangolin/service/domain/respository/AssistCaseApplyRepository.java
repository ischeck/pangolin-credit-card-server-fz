package cn.fintecher.pangolin.service.domain.respository;


import cn.fintecher.pangolin.entity.domain.AssistCaseApply;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by huyanmin on 2018/7/11.
 */
public interface AssistCaseApplyRepository extends ElasticsearchRepository<AssistCaseApply, String> {
}

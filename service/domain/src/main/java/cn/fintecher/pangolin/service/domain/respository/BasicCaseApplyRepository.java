package cn.fintecher.pangolin.service.domain.respository;


import cn.fintecher.pangolin.entity.domain.BasicCaseApply;
import cn.fintecher.pangolin.entity.managentment.ApproveFlowConfig;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/**
 * Created by huyanmin on 2018/7/18.
 */
public interface BasicCaseApplyRepository extends ElasticsearchRepository<BasicCaseApply, String>{
}

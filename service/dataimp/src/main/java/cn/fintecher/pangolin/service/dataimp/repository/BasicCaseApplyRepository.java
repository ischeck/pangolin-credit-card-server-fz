package cn.fintecher.pangolin.service.dataimp.repository;


import cn.fintecher.pangolin.entity.domain.BasicCaseApply;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by huyanmin on 2018/7/18.
 */
public interface BasicCaseApplyRepository extends ElasticsearchRepository<BasicCaseApply, String>{
}

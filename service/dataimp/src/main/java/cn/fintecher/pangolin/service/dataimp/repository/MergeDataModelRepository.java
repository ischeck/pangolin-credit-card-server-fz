package cn.fintecher.pangolin.service.dataimp.repository;

import cn.fintecher.pangolin.entity.domain.MergeDataModel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 15:25 2018/9/27
 */
public interface MergeDataModelRepository  extends ElasticsearchRepository<MergeDataModel, String> {
}

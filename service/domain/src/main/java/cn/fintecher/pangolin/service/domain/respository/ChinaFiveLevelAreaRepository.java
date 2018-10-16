package cn.fintecher.pangolin.service.domain.respository;


import cn.fintecher.pangolin.entity.domain.ChinaFiveLevelArea;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by ChenChang on 2018/7/17.
 */
public interface ChinaFiveLevelAreaRepository extends ElasticsearchRepository<ChinaFiveLevelArea, String> {
}

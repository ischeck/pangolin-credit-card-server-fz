package cn.fintecher.pangolin.service.repair.respository;

import cn.fintecher.pangolin.entity.repair.Relationship;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by hanwannan on 2017/8/27.
 */
public interface RelationshipRepository extends ElasticsearchRepository<Relationship, String> {

}

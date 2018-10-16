package cn.fintecher.pangolin.service.repair.respository;

import cn.fintecher.pangolin.entity.repair.KosekiRemark;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by hanwannan on 2017/8/27.
 */
public interface KosekiRemarkRepository extends ElasticsearchRepository<KosekiRemark, String> {

}

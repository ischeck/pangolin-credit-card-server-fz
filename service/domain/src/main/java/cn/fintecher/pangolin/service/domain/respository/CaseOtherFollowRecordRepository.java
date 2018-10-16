package cn.fintecher.pangolin.service.domain.respository;

import cn.fintecher.pangolin.entity.domain.CaseOtherFollowRecord;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by BBG on 2018/8/2.
 */
public interface CaseOtherFollowRecordRepository extends ElasticsearchRepository<CaseOtherFollowRecord,String> {

}

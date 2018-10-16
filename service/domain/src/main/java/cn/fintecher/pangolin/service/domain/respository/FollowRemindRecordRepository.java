package cn.fintecher.pangolin.service.domain.respository;

import cn.fintecher.pangolin.entity.domain.FollowRemindRecord;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface FollowRemindRecordRepository extends ElasticsearchRepository<FollowRemindRecord,String> {

}

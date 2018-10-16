package cn.fintecher.pangolin.service.domain.respository;

import cn.fintecher.pangolin.entity.domain.CaseFindRecord;
import cn.fintecher.pangolin.entity.domain.CaseFollowupRecord;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by BBG on 2018/8/2.
 */
public interface CaseFindRecordRepository extends ElasticsearchRepository<CaseFindRecord,String> {

}

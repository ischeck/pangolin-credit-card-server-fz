package cn.fintecher.pangolin.service.dataimp.repository;

import cn.fintecher.pangolin.entity.domain.CaseFollowupRecord;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by BBG on 2018/8/2.
 */
public interface CaseFollowupRecordRepository extends ElasticsearchRepository<CaseFollowupRecord,String> {

}

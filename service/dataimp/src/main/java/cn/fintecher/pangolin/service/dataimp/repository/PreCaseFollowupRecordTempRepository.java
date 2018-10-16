package cn.fintecher.pangolin.service.dataimp.repository;

import cn.fintecher.pangolin.entity.domain.PreCaseFollowupRecordTemp;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 18:31 2018/8/3
 */
public interface PreCaseFollowupRecordTempRepository extends ElasticsearchRepository<PreCaseFollowupRecordTemp,String> {
    Iterable<PreCaseFollowupRecordTemp> findByOperBatchNumberAndOperator(String operBatchNumber,String operator);
}


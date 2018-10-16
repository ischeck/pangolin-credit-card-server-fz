package cn.fintecher.pangolin.service.dataimp.repository;

import cn.fintecher.pangolin.entity.domain.CaseEndImportTemp;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 16:12 2018/8/2
 */
public interface CaseEndImportTempRepository extends ElasticsearchRepository<CaseEndImportTemp,String> {
    Iterable<CaseEndImportTemp> findByOperBatchNumberAndOperator(String operBatchNumber,String operator);
}

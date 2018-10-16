package cn.fintecher.pangolin.service.dataimp.repository;

import cn.fintecher.pangolin.entity.domain.CaseLeafImportTemp;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 16:13 2018/8/2
 */
public interface CaseLeafImportTempRepository extends ElasticsearchRepository<CaseLeafImportTemp,String> {
    Iterable<CaseLeafImportTemp> findByOperBatchNumberAndOperator(String operBatchNumber,String operator);
}

package cn.fintecher.pangolin.service.dataimp.repository;

import cn.fintecher.pangolin.entity.domain.CaseWarnImportTemp;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 16:23 2018/8/2
 */
public interface CaseWarnImportTempRepository extends ElasticsearchRepository<CaseWarnImportTemp,String> {
    Iterable<CaseWarnImportTemp> findByOperBatchNumberAndOperator(String operBatchNumber,String operator);
}

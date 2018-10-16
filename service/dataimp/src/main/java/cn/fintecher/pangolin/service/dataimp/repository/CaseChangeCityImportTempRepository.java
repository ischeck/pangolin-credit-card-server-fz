package cn.fintecher.pangolin.service.dataimp.repository;

import cn.fintecher.pangolin.entity.domain.CaseChangeCityImportTemp;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 16:10 2018/8/2
 */
public interface CaseChangeCityImportTempRepository extends ElasticsearchRepository<CaseChangeCityImportTemp,String> {
    Iterable<CaseChangeCityImportTemp> findByOperBatchNumberAndOperator(String operBatchNumber,String operator);
}

package cn.fintecher.pangolin.service.dataimp.repository;

import cn.fintecher.pangolin.entity.domain.CaseBillImportTemp;
import cn.fintecher.pangolin.entity.domain.CaseWorkImportOrderInfoTemp;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 16:08 2018/8/2
 */
public interface CaseWorkOrderImportTempRepository extends ElasticsearchRepository<CaseWorkImportOrderInfoTemp,String> {
    Iterable<CaseWorkImportOrderInfoTemp> findByOperBatchNumberAndOperator(String operBatchNumber, String operator);
}

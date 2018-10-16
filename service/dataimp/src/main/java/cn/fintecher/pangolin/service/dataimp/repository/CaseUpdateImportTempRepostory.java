package cn.fintecher.pangolin.service.dataimp.repository;

import cn.fintecher.pangolin.entity.domain.CaseUpdateImportTemp;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author:peishouwen
 * @Desc: 案件更新导入
 * @Date:Create in 10:37 2018/8/2
 */
public interface CaseUpdateImportTempRepostory extends ElasticsearchRepository<CaseUpdateImportTemp,String> {

    Iterable<CaseUpdateImportTemp> findByOperBatchNumberAndOperator(String operBatchNumber,String operator);

}

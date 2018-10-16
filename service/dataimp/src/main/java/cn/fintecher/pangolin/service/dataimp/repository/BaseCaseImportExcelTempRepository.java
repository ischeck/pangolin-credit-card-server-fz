package cn.fintecher.pangolin.service.dataimp.repository;

import cn.fintecher.pangolin.entity.domain.BaseCaseAllImportExcelTemp;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 9:47 2018/7/26
 */
public interface BaseCaseImportExcelTempRepository extends ElasticsearchRepository<BaseCaseAllImportExcelTemp,String> {


    Iterable<BaseCaseAllImportExcelTemp> findAllByBatchNumberAndOperator(String batchNumber, String operator);

    Iterable<BaseCaseAllImportExcelTemp> findAllByBatchNumber(String batchNumber);

    long countByBatchNumberAndPrincipalId(String batchNumber,String principalId);


}

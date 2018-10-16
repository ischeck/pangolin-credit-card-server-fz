package cn.fintecher.pangolin.service.dataimp.repository;

import cn.fintecher.pangolin.entity.domain.BasePersonalImportExcelTemp;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 18:37 2018/7/27
 */
public interface BasePersonalImportExcelTempRepository extends ElasticsearchRepository<BasePersonalImportExcelTemp,String> {


   Iterable<BasePersonalImportExcelTemp> findByBatchNumber(String batchNumber);

   List<BasePersonalImportExcelTemp> findByBatchNumberAndId(String batchNumber, String id);


}

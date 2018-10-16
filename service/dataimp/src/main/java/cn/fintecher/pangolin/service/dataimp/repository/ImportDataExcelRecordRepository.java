package cn.fintecher.pangolin.service.dataimp.repository;

import cn.fintecher.pangolin.entity.domain.ImportDataExcelRecord;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 14:46 2018/7/28
 */
public interface ImportDataExcelRecordRepository extends ElasticsearchRepository<ImportDataExcelRecord,String> {

    ImportDataExcelRecord findByBatchNumber(String batchNumber);

}

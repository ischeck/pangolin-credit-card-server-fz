package cn.fintecher.pangolin.service.dataimp.repository;

import cn.fintecher.pangolin.entity.domain.ImportOthersDataExcelRecord;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 9:57 2018/8/2
 */
public interface ImportOthersDataExcelRecordRepository extends ElasticsearchRepository<ImportOthersDataExcelRecord,String> {

    ImportOthersDataExcelRecord findByIdAndOperatorUserName(String id,String operatorName);
}

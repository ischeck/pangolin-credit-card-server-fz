package cn.fintecher.pangolin.service.repair.respository;

import cn.fintecher.pangolin.entity.repair.DataRepairImportRecord;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by hanwannan on 2017/9/1.
 */
public interface DataRepairImportRespository extends ElasticsearchRepository<DataRepairImportRecord, String> {

}

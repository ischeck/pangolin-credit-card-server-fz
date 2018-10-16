package cn.fintecher.pangolin.service.dataimp.repository;

import cn.fintecher.pangolin.entity.domain.CaseWorkOrderInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 13:56 2018/8/7
 */
public interface ImportCaseWorkOrderInfoRepository extends ElasticsearchRepository<CaseWorkOrderInfo,String> {

}

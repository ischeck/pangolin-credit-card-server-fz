package cn.fintecher.pangolin.service.dataimp.repository;

import cn.fintecher.pangolin.entity.domain.CaseWarningInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 16:35 2018/8/7
 */
public interface ImpCaseWarningInfoRepository extends ElasticsearchRepository<CaseWarningInfo,String> {
}

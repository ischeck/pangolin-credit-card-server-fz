package cn.fintecher.pangolin.service.domain.respository;

import cn.fintecher.pangolin.entity.domain.CaseWarningInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 16:35 2018/8/7
 */
public interface CaseWarningInfoRepository extends ElasticsearchRepository<CaseWarningInfo,String> {
}

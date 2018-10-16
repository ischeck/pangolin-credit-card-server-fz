package cn.fintecher.pangolin.service.dataimp.repository;

import cn.fintecher.pangolin.entity.domain.LeftAmtLog;
import org.apache.catalina.LifecycleState;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 15:42 2018/8/8
 */
public interface ImpLeftAmtLogRepPository extends ElasticsearchRepository<LeftAmtLog,String> {
}

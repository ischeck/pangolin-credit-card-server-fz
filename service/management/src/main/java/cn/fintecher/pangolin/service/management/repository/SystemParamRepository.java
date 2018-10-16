package cn.fintecher.pangolin.service.management.repository;

import cn.fintecher.pangolin.entity.managentment.SysParam;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;


/**
 * @Author huyanmin
 * @Date 2018/06/27
 */

public interface SystemParamRepository extends MongoRepository<SysParam, String>,
        QuerydslPredicateExecutor<SysParam> {

}

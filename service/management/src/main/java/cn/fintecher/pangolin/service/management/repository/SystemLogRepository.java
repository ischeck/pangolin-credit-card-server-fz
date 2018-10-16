package cn.fintecher.pangolin.service.management.repository;

import cn.fintecher.pangolin.entity.managentment.SystemLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;


/**
 * @Author huyanmin
 * @Date 2018/06/29
 */

public interface SystemLogRepository extends MongoRepository<SystemLog, String>,
        QuerydslPredicateExecutor<SystemLog> {

}

package cn.fintecher.pangolin.service.common.respository;

import cn.fintecher.pangolin.service.common.model.TaskBox;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/**
 * @Author:huyanmin
 * @Desc:
 * @Date:Create 2018/9/18
 */
public interface TaskBoxRepository extends QuerydslPredicateExecutor<TaskBox>, MongoRepository<TaskBox, String> {
}

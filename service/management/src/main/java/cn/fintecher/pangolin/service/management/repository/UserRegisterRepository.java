package cn.fintecher.pangolin.service.management.repository;

import cn.fintecher.pangolin.entity.managentment.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/**
 * Created by huyanmin on 2018/8/29
 */
public interface UserRegisterRepository extends MongoRepository<User, String>,
        QuerydslPredicateExecutor<User> {

    User findByEmployeeNumber(String employeeNumber);
}

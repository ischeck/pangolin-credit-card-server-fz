package cn.fintecher.pangolin.service.management.repository;

import cn.fintecher.pangolin.entity.managentment.Principal;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;


/**
 *
 * @Author huyanmin
 * @Date 2018/06/26
 *
 */

public interface PrincipalRepository extends MongoRepository<Principal, String>,
        QuerydslPredicateExecutor<Principal> {

}

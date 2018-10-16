package cn.fintecher.pangolin.service.management.repository;

import cn.fintecher.pangolin.entity.managentment.ContactResult;
import cn.fintecher.pangolin.entity.managentment.CustConfig;
import cn.fintecher.pangolin.entity.managentment.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;


/**
 * Created by BBG on 2018/6/7
 */
public interface CustConfigRepository extends MongoRepository<CustConfig, String>,
        QuerydslPredicateExecutor<CustConfig> {

}

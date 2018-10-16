package cn.fintecher.pangolin.service.management.repository;

import cn.fintecher.pangolin.entity.managentment.ClockConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ClockConfigRepository extends MongoRepository<ClockConfig,String>, QuerydslPredicateExecutor<ClockConfig> {

}

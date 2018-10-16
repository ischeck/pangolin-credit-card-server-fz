package cn.fintecher.pangolin.service.management.repository;

import cn.fintecher.pangolin.entity.managentment.MobilePosition;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface MobilePositionRepository extends MongoRepository<MobilePosition,String>,
        QuerydslPredicateExecutor<MobilePosition> {

}

package cn.fintecher.pangolin.service.management.repository;


import cn.fintecher.pangolin.entity.managentment.ClockRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ClockRecordRepository extends MongoRepository<ClockRecord,String>, QuerydslPredicateExecutor<ClockRecord> {

}

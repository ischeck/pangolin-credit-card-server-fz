package cn.fintecher.pangolin.service.management.repository;

import cn.fintecher.pangolin.entity.managentment.Notice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface NoticeRepository extends MongoRepository<Notice,String>, QuerydslPredicateExecutor<Notice> {
}

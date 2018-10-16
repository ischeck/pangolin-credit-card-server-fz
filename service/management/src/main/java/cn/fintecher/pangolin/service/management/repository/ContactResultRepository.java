package cn.fintecher.pangolin.service.management.repository;

import cn.fintecher.pangolin.entity.managentment.ContactResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;


/**
 * Created by BBG on 2018/6/7
 */
public interface ContactResultRepository extends MongoRepository<ContactResult, String>,
        QuerydslPredicateExecutor<ContactResult> {

    List<ContactResult> findByPid(String pid);
}

package cn.fintecher.pangolin.service.management.repository;

import cn.fintecher.pangolin.entity.managentment.Color;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;


/**
 * Created by BBG on 2018/6/7
 */
public interface ColorRepository extends MongoRepository<Color, String>,
        QuerydslPredicateExecutor<Color> {

}

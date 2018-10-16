package cn.fintecher.pangolin.service.management.repository;

import cn.fintecher.pangolin.entity.managentment.ImportExcelConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;


/**
 * Created by ChenChang on 2018/6/7
 */
public interface ImportExcelConfigRepository extends MongoRepository<ImportExcelConfig, String>,
        QuerydslPredicateExecutor<ImportExcelConfig> {
}

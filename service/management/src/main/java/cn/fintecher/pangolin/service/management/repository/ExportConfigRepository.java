package cn.fintecher.pangolin.service.management.repository;

import cn.fintecher.pangolin.entity.managentment.ExportConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ExportConfigRepository extends MongoRepository<ExportConfig, String>,
        QuerydslPredicateExecutor<ExportConfig> {
}

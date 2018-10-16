package cn.fintecher.pangolin.service.management.repository;

import cn.fintecher.pangolin.entity.managentment.ComplianceConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/**
 * 合规配置Repository
 * Created by ChenChang on 2018/8/31.
 */
public interface ComplianceConfigRepository extends MongoRepository<ComplianceConfig, String>,
        QuerydslPredicateExecutor<ComplianceConfig> {
}

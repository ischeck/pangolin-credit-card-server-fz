package cn.fintecher.pangolin.service.management.repository;

import cn.fintecher.pangolin.entity.managentment.ApproveFlowConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;


/**
 * Created by ChenChang on 2018/7/24
 */
public interface ConfigFlowRepository extends MongoRepository<ApproveFlowConfig, String>, QuerydslPredicateExecutor<ApproveFlowConfig> {

}

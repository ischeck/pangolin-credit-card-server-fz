package cn.fintecher.pangolin.service.management.repository;

import cn.fintecher.pangolin.entity.managentment.Organization;
import io.swagger.annotations.Api;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;


/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 14:36 2018/6/7
 */
@Api(description = "组织机构知识库")
public interface OrganizationRepository extends MongoRepository<Organization,String>,
        QuerydslPredicateExecutor<Organization> {
}

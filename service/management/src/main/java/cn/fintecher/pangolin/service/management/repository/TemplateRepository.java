package cn.fintecher.pangolin.service.management.repository;

import cn.fintecher.pangolin.entity.managentment.Template;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface TemplateRepository extends MongoRepository<Template, String>,
        QuerydslPredicateExecutor<Template> {

}

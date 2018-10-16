package cn.fintecher.pangolin.service.management.repository;

import cn.fintecher.pangolin.entity.managentment.Role;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;


/**
 * Created by ChenChang on 2018/6/7
 */
public interface RoleRepository extends MongoRepository<Role, String>, QuerydslPredicateExecutor<Role> {
    @Override
    @CacheEvict(value = {"role.all","role.byRoleId"},allEntries = true)
    <S extends Role> S save(S entity);

    @Override
    // @Cacheable(value = "role.byRoleId",keyGenerator  = "firstParamKeyGenerator")
    Optional<Role> findById(String id);

    @Override
    @Cacheable("role.all")
     List<Role> findAll()  ;

}

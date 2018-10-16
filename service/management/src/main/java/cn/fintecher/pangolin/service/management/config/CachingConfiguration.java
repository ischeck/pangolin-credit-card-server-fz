package cn.fintecher.pangolin.service.management.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.lang.reflect.Method;

/**
 * Created by ChenChang on 2018/6/15.
 */
@Configuration
@EnableCaching
public class CachingConfiguration {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheManager redisCacheManager = RedisCacheManager.create(connectionFactory);
        return redisCacheManager;
    }

    /**
     * 生成key的策略【自定义第三种】
     * 使用范围：仅适用于选取第一个参数做键的情况
     * 由于reposotory上不能直接使用spel表达式作key，故而采用key的生成策略的方式来替换
     *
     * 使用时在注解@Cacheable(value = "admins",keyGenerator = "firstParamKeyGenerator")中指定
     * @return
     */
    @Bean(name = "firstParamKeyGenerator")
    public KeyGenerator firstParamKeyGenerator(){
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(params[0].toString());
            return sb.toString();
        };
    }

}
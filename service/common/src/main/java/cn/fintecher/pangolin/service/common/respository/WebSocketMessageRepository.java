package cn.fintecher.pangolin.service.common.respository;

import cn.fintecher.pangolin.service.common.model.WebSocketMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 11:30 2018/9/4
 */
public interface WebSocketMessageRepository  extends QuerydslPredicateExecutor<WebSocketMessage>, MongoRepository<WebSocketMessage, String> {
}

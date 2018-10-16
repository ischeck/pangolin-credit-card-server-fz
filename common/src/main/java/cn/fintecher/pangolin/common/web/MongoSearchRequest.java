package cn.fintecher.pangolin.common.web;


import com.querydsl.core.BooleanBuilder;

/**
 * 使用MongoDB查询请求
 */
public abstract class MongoSearchRequest {
    public abstract BooleanBuilder generateQueryBuilder();
}

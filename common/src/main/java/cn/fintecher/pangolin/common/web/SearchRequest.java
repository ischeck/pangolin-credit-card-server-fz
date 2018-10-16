package cn.fintecher.pangolin.common.web;

import org.elasticsearch.index.query.QueryBuilder;

/**
 * 使用elasticsearch查询请求
 */
public abstract class SearchRequest {
    public abstract QueryBuilder generateQueryBuilder();
}

package cn.fintecher.pangolin.service.dataimp.repository;

import cn.fintecher.pangolin.entity.domain.Comment;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface ImpCommentRepository extends ElasticsearchRepository<Comment,String> {

}

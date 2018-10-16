package cn.fintecher.pangolin.service.domain.respository;


import cn.fintecher.pangolin.entity.domain.Comment;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by huyanmin on 2018/8/6.
 */
public interface CommentRepository extends ElasticsearchRepository<Comment, String> {
}

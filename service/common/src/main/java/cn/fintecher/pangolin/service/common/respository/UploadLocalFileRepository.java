package cn.fintecher.pangolin.service.common.respository;

import cn.fintecher.pangolin.service.common.model.UploadLocalFile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface UploadLocalFileRepository extends QuerydslPredicateExecutor<UploadLocalFile>, MongoRepository<UploadLocalFile, String> {

}

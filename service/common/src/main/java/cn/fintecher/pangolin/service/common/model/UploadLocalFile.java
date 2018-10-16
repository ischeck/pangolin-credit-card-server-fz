package cn.fintecher.pangolin.service.common.model;

import cn.fintecher.pangolin.common.model.UploadFile;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by ChenChang on 2017/12/22.
 */
@Document
@Data
public class UploadLocalFile extends UploadFile {
    @Id
    private String id;
}

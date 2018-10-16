package cn.fintecher.pangolin.service.common.service;

import cn.fintecher.pangolin.common.model.UploadFile;
import cn.fintecher.pangolin.common.utils.Snowflake;
import cn.fintecher.pangolin.service.common.model.QUploadLocalFile;
import cn.fintecher.pangolin.service.common.model.UploadLocalFile;
import cn.fintecher.pangolin.service.common.respository.UploadLocalFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Objects;

/**
 * Created by ChenChang on 2017/12/26.
 */
@Service("uploadFileService")
public class UploadFileService {
    private final Logger log = LoggerFactory.getLogger(UploadFileService.class);
    @Autowired
    private UploadLocalFileRepository uploadLocalFileRepository;
    @Value("${file.dir}")
    private String fileDir;

    @Value("${file.base-url}")
    private String baseUrl;
    @Autowired
    private Snowflake snowflake;

    public UploadFile findOne(String id) {
        UploadFile uploadFile = uploadLocalFileRepository.findById(id).get();
        uploadFile.setUrl(baseUrl + "/" + uploadFile.getFileName());
        return uploadFile;
    }

    public File findOneFileById(String id) throws Exception{
        UploadFile uploadFile = findOne(id);
        File file = new File(fileDir + "/" + uploadFile.getFileName());
        return file;
    }

    public File findOneFileByName(String fileName) {
        QUploadLocalFile qUploadFile = QUploadLocalFile.uploadLocalFile;
        UploadFile uploadFile = uploadLocalFileRepository.findOne(qUploadFile.fileName.contains(fileName)).get();
        File file = new File(fileDir + "/" + uploadFile.getFileName());
        return file;
    }
    public UploadFile fileUpload(MultipartFile file) throws Exception {
        UploadFile uploadFile = fileUpload(file.getInputStream(), file.getOriginalFilename());
        return uploadFile;

    }

    public UploadFile fileUpload(InputStream inputStream, String originalName) throws Exception {
        String extensionName = originalName.substring(originalName.lastIndexOf(".") + 1);
        String fileId = String.valueOf(snowflake.next());
        String fileName = fileId + "." + extensionName;
        saveToDisk(inputStream, extensionName, fileId);
        UploadFile uploadFile = saveUploadFile(fileId, fileName, originalName, extensionName);
        inputStream.close();
        return uploadFile;

    }

    /**
     * 文件写入硬盘
     * @param inputStream
     * @param extensionName
     * @param fileId
     * @throws IOException
     */
    private void saveToDisk(InputStream inputStream, String extensionName, String fileId) throws Exception {
        File outFile =null;
        OutputStream ot =null;
        try {
            outFile = new File(fileDir);
            if (!outFile.exists()) {
                outFile.mkdirs();
            }
            String fileName = fileId + "." + extensionName;
            ot = new FileOutputStream(fileDir + "/" + fileName);
            byte[] buffer = new byte[1024];
            int len;
            while ((-1 != (len = inputStream.read(buffer)))) {
                ot.write(buffer, 0, len);
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
            throw new Exception("file.saveToDisk.error");
        }finally {
            if(Objects.nonNull(ot)){
                ot.close();
            }
        }
    }

    /**
     * 保存文件记录到数据库
     * @param fileId
     * @param fileName
     * @param originalName
     * @param extensionName
     * @return
     */
    private UploadFile saveUploadFile(String fileId, String fileName,String originalName, String extensionName) throws Exception{
        try {
            UploadLocalFile uploadFile = new UploadLocalFile();
            uploadFile.setOriginalName(originalName);
            uploadFile.setExtensionName(extensionName);
            uploadFile.setFileName(fileName);
            uploadFile.setId(fileId);
            uploadFile.setUrl(fileName);
            return uploadLocalFileRepository.save(uploadFile);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            throw new Exception("file.saveRecord.error");
        }

    }

}

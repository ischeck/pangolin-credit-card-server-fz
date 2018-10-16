package cn.fintecher.pangolin.service.common.web;

import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.UploadFile;
import cn.fintecher.pangolin.service.common.service.UploadFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by ChenChang on 2017/12/26.
 */
@RestController
@RequestMapping("/api/uploadFile")
@Api(value = "文件上传", description = "文件上传")
public class UploadFileController {
    private final Logger log = LoggerFactory.getLogger(UploadFileController.class);
    @Autowired
    private UploadFileService uploadFileService;

    @Value("${file.dir}")
    private String fileDir;
    @Value("${file.base-url}")
    private String baseUrl;

    @ApiOperation(value = "文件上传", notes = "文件上传")
    @PostMapping("/upload")
    public ResponseEntity<UploadFile> fileUpload(@RequestParam("file") MultipartFile file) {
        try {
            UploadFile uploadFile = uploadFileService.fileUpload(file);
            uploadFile.setUrl(baseUrl + "/" + uploadFile.getFileName());
            return ResponseEntity.ok().body(uploadFile);
        } catch (Exception e) {
            throw new BadRequestException(null, "fileUpload", Objects.isNull(e.getMessage()) ? "fileUpload.is.fail" : e.getMessage());
        }
    }

    @ApiOperation(value = "通过文件名下载文件", notes = "通过文件名下载文件")
    @GetMapping("/getFile/{fileName}")
    public void getFile(@PathVariable String fileName, HttpServletResponse response) {
        log.debug("开始下载文件");
        File file = uploadFileService.findOneFileByName(fileName);
        response.setContentType("multipart/octet-stream");
        response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);
        ServletOutputStream out;
        try {
            FileInputStream inputStream = new FileInputStream(file);
            out = response.getOutputStream();
            int b = 0;
            byte[] buffer = new byte[1024];
            while (b != -1) {
                b = inputStream.read(buffer);
                out.write(buffer, 0, b);
            }
            inputStream.close();
            out.close();
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "通过文件ID载文件", notes = "通过文件ID载文件")
    @GetMapping("/getFileStreamById/{fileId}")
    public void getFileStreamById(@PathVariable String fileId, HttpServletResponse response) {
        log.debug("开始下载文件");
        UploadFile uploadFile = uploadFileService.findOne(fileId);
        if (Objects.isNull(uploadFile)) {
            throw new BadRequestException(null, "getFileStreamById", "file.is.notExist");
        }
        File file = new File(fileDir + "/" + uploadFile.getFileName());
        response.setContentType("multipart/octet-stream");
        response.setHeader("Content-Disposition", "attachment;fileName=" + uploadFile.getFileName());
        ServletOutputStream out = null;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            out = response.getOutputStream();
            int b = 0;
            byte[] buffer = new byte[1024];
            while (b != -1) {
                b = inputStream.read(buffer);
                out.write(buffer, 0, b);
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (Objects.nonNull(inputStream)) {
                    inputStream.close();
                }
                if (Objects.nonNull(out)) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @ApiOperation(value = "通过文件ID下载文件", notes = "通过文件ID下载文件")
    @GetMapping("/getFileById/{fileId}")
    public ResponseEntity<String> getFileById(@PathVariable String fileId) {
        log.debug("开始下载文件");
        try {
            File file = uploadFileService.findOneFileById(fileId);
            return ResponseEntity.ok().body(file.getPath());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BadRequestException(null, "getFileById", Objects.isNull(e.getMessage()) ? "getFileById.is.fail" : e.getMessage());
        }

    }

    @ApiOperation(value = "按ID查询", notes = "按ID查询")
    @GetMapping("/getUploadFile/{id}")
    public ResponseEntity<UploadFile> getUploadFile(@PathVariable String id) {
        log.debug("REST request to get UploadFile : {}", id);
        UploadFile uploadFile = uploadFileService.findOne(id);
        uploadFile.setUrl(baseUrl + "/" + uploadFile.getFileName());
        return Optional.ofNullable(uploadFile)
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "按ID查询文件URL", notes = "按ID查询文件URL")
    @GetMapping("/getFileUrlById/{id}")
    public ResponseEntity<String> getFileUrlById(@PathVariable String id){
        UploadFile uploadFile = uploadFileService.findOne(id);
        return Optional.ofNullable(uploadFile)
                .map(result -> new ResponseEntity<>(
                        baseUrl.concat("/").concat(uploadFile.getFileName()),
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


}

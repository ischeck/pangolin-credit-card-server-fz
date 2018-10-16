package cn.fintecher.pangolin.service.repair.web;

import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.utils.InnerServiceUrl;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.repair.DataRepairImportRecord;
import cn.fintecher.pangolin.service.repair.model.request.DataRepairImportRequest;
import cn.fintecher.pangolin.service.repair.respository.DataRepairImportRespository;
import cn.fintecher.pangolin.service.repair.service.DataRepairBaseService;
import cn.fintecher.pangolin.service.repair.service.DataRepairImportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by hanwannan on 2017/9/1.
 */
@RestController
@RequestMapping("/api/dataRepairImport")
@Api(value = "数据修复资料导入", description = "数据修复资料导入")
public class DataRepairImportController {
    private final Logger log = LoggerFactory.getLogger(DataRepairImportController.class);

    @Autowired
    private DataRepairImportRespository dataRepairImportRespository;
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    DataRepairImportService dataRepairImportService;
    @Autowired
    DataRepairBaseService dataRepairBaseService;

    @PostMapping("/import")
    @ApiOperation(value = "资料导入", notes = "资料导入")
    public ResponseEntity importData(@Valid @RequestBody DataRepairImportRequest request,
                                     @RequestHeader(value = "X-UserToken") @ApiParam("操作者的Token") String token) {
    		List<String> errorList=new ArrayList<>();
        try {
            //获取用户信息
            OperatorModel operator=dataRepairBaseService.getUserByToken(token);

            //获取文件
            String fileId=request.getFileId();
            HttpHeaders headers = new HttpHeaders();
            ResponseEntity<byte[]> response = restTemplate.exchange(InnerServiceUrl.COMMON_SERVICE_GETFILEBYID.concat("/").concat(fileId),
                    HttpMethod.GET, new HttpEntity<byte[]>(headers), byte[].class);
            List<String> content=response.getHeaders().get("Content-Disposition");
            if(Objects.isNull(content) || content.isEmpty()){
                throw new Exception("templateFile.is.illegal");
            }
//            if(!content.get(0).endsWith(SaxParseExcelUtil.EXCEL_TYPE_XLSX)){
//                log.error("fileName: {} ",content.get(0));
//                throw  new Exception("file.format.error");
//            }
            byte[] result = response.getBody();
            InputStream inputStream=new ByteArrayInputStream(result);

            //数据库添加导入记录
            DataRepairImportRecord dataRepairImportRecord=new DataRepairImportRecord();
            dataRepairImportRecord.setFileId(request.getFileId());
            dataRepairImportRecord.setOperatorTime(ZWDateUtil.getNowDateTime());
            dataRepairImportRecord.setOperatorName(operator.getFullName());
            dataRepairImportRecord.setOperatorUserName(operator.getUsername());
            dataRepairImportRecord.setImportContentType(request.getImportContentType());
            dataRepairImportRespository.save(dataRepairImportRecord);

            //数据导入
            dataRepairImportService.importExcelData(request, inputStream, errorList);
            if(errorList.size()>0){
                return ResponseEntity.ok().body(errorList);
            }
            return ResponseEntity.ok().body(null);
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw  new BadRequestException(null, "importExcelData", Objects.isNull(e.getMessage()) ? "importExcelData.is.fail":e.getMessage());
        }
    }

}

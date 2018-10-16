package cn.fintecher.pangolin.service.dataimp.web;

import cn.fintecher.pangolin.common.enums.ImportDataExcelStatus;
import cn.fintecher.pangolin.common.enums.TemplateType;
import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.model.RemarkModel;
import cn.fintecher.pangolin.common.utils.InnerServiceUrl;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.domain.*;
import cn.fintecher.pangolin.entity.managentment.ImportExcelConfig;
import cn.fintecher.pangolin.entity.managentment.ImportExcelConfigItem;
import cn.fintecher.pangolin.service.dataimp.model.request.BaseCaseAllImportExcelTempRequest;
import cn.fintecher.pangolin.service.dataimp.model.request.BaseCaseImportExcelRequest;
import cn.fintecher.pangolin.common.model.CaseInfoPropertyResponse;
import cn.fintecher.pangolin.service.dataimp.repository.BaseCaseImportExcelTempRepository;
import cn.fintecher.pangolin.service.dataimp.repository.ImportDataExcelRecordRepository;
import cn.fintecher.pangolin.service.dataimp.service.BaseCaseImportExcelTempService;
import cn.fintecher.pangolin.service.dataimp.service.BatchNumberSeqService;
import cn.fintecher.pangolin.service.dataimp.service.DataimpBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.io.InputStream;
import java.util.*;


/**
 * Created by huyanmin on 2018/7/4.
 */
@RestController
@RequestMapping("/api/importExcelCaseController")
@Api(value = "数据Excel导入", description = "数据Excel导入")
public class ImportExcelCaseController {
    private final Logger logger = LoggerFactory.getLogger(ImportExcelCaseController.class);

    @Autowired
    BaseCaseImportExcelTempService caseImportExcelTempService;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    BatchNumberSeqService batchNumberSeqService;

    @Autowired
    ImportDataExcelRecordRepository importDataExcelRecordRepository;

    @Autowired
    DataimpBaseService dataimpBaseService;

    @Autowired
    BaseCaseImportExcelTempRepository baseCaseImportExcelTempRepository;

    @GetMapping("/getCaseInfoProperty")
    @ApiOperation(value = "获取案件信息内置字段", notes = "获取案件信息内置字段")
    public ResponseEntity<List<CaseInfoPropertyResponse>> getCaseInfoProperty(@RequestParam(name = "templateType",required = true) TemplateType templateType) {
        logger.debug("REST request to create TemplateExcelRelatedInfo");
       try {
           List<Class<?>> objClassList=new ArrayList<>();
           switch (templateType){
               case IMPORT_CASE:
                   objClassList.add(BaseCaseAllImportExcelTemp.class);
                   objClassList.add(BasePersonalImportExcelTemp.class);
                   break;
               case IMPORT_UPDATE_CASE:
                   objClassList.add(CaseUpdateImportTemp.class);
                   break;
               case IMPORT_WARNING_INFO:
                   objClassList.add(CaseWarnImportTemp.class);
                   break;
               case IMPORT_WORKER_ORDER:
                   objClassList.add(CaseWorkImportOrderInfoTemp.class);
                   break;
               case  IMPORT_BILL:
                   objClassList.add(CaseBillImportTemp.class);
                   break;
               case  IMPORT_LEFT_CASE:
                   objClassList.add(CaseLeafImportTemp.class);
                   break;
               case IMPORT_CHANGE_CITY:
                   objClassList.add(CaseChangeCityImportTemp.class);
                   break;
               case IMPORT_FOLLOW_RECORD:
                   break;
               case IMPORT_END_CASE:
                   objClassList.add(CaseEndImportTemp.class);
                   break;
               default:
                   throw new Exception("templateType.is.error");
           }

           List<CaseInfoPropertyResponse> responses=caseImportExcelTempService.getObjejctPro(objClassList);
           return ResponseEntity.ok().body(responses);
       }catch (Exception e){
           logger.error(e.getMessage(),e);
           throw  new BadRequestException(null, "getCaseInfoProperty", Objects.isNull(e.getMessage()) ? "getCaseInfoProperty.is.fail":e.getMessage());
       }
    }



    @PostMapping("/importExcelData")
    @ApiOperation(value = "案件导入", notes = "案件导入")
    public ResponseEntity importExcelData(@Valid @RequestBody BaseCaseImportExcelRequest baseCaseImportExcelRequest,
                                                @RequestHeader(value = "X-UserToken") @ApiParam("操作者的Token") String token) {
        try {
            //获取用户信息
            OperatorModel operator=dataimpBaseService.getUserByToken(token);
            ImportExcelConfig importExcelConfig=null;
            List<ImportExcelConfigItem> items=null;
            //获取模板配置
            try {
                ResponseEntity<ImportExcelConfig>  importExcelConfigResponseEntity=restTemplate.getForEntity(InnerServiceUrl.MANAGEMENT_SERVICE_GETTEMPLATEBYID.concat(baseCaseImportExcelRequest.getTemplateId()),ImportExcelConfig.class);
                importExcelConfig=importExcelConfigResponseEntity.getBody();
                items=importExcelConfig.getItems();
            }catch (Exception e){
                logger.error(e.getMessage(),e);
                throw  new BadRequestException(null, "importExcelData", "importExcelData.importExcelConfig.fail");
            }
            InputStream inputStream = caseImportExcelTempService.readFile(baseCaseImportExcelRequest.getFileId());
            //批次号
            if(StringUtils.isBlank(baseCaseImportExcelRequest.getBatchNumber())){
                baseCaseImportExcelRequest.setBatchNumber(batchNumberSeqService.getBatchNumberSeq());
            }else {
                //验证批次号是否存在
               if(caseImportExcelTempService.checkBatchNumberExist(baseCaseImportExcelRequest)){
                   throw  new BadRequestException(null, "importExcelData", "importExcelData.batchNumber.exist");
               }
            }
            ModelMapper modelMapper=new ModelMapper();
            modelMapper.addMappings(new PropertyMap<BaseCaseImportExcelRequest, ImportDataExcelRecord>() {
                protected void configure() {
                    skip().setId(null);
                }});
            ImportDataExcelRecord importDataExcelRecord=new ImportDataExcelRecord();
            modelMapper.map(baseCaseImportExcelRequest,importDataExcelRecord);
            importDataExcelRecord.setOperatorTime(ZWDateUtil.getNowDateTime());
            importDataExcelRecord.setOperatorName(operator.getFullName());
            importDataExcelRecord.setOperatorUserName(operator.getUsername());
            importDataExcelRecord.setImportDataExcelStatus(ImportDataExcelStatus.IMPORTING);
            importDataExcelRecord=importDataExcelRecordRepository.save(importDataExcelRecord);
            //异步请求操作
            caseImportExcelTempService.importExcelData(baseCaseImportExcelRequest,operator,importExcelConfig,items,inputStream,importDataExcelRecord);
            return ResponseEntity.ok().body(null);
        } catch (final Exception e) {
           logger.error(e.getMessage(), e);
          throw  new BadRequestException(null, "importExcelData", Objects.isNull(e.getMessage()) ? "importExcelData.is.fail":e.getMessage());
        }
    }

    @GetMapping("/getExcelData")
    @ApiOperation(value = "获取导入临时信息", notes = "获取导入临时信息")
    public ResponseEntity<Page<BaseCaseAllImportExcelTemp>> getExcelData(@RequestHeader(value = "X-UserToken") @ApiParam("操作者的Token") String token,
                                                                       Pageable pageable,
                                                                       BaseCaseAllImportExcelTempRequest request) {
        try{
            Page<BaseCaseAllImportExcelTemp> page = baseCaseImportExcelTempRepository.search(request.generateQueryBuilder(),pageable);

            return ResponseEntity.ok().body(page);
        }catch(Exception e){
            logger.error(e.getMessage(),e);
            throw  new BadRequestException(null, "importExcelData", Objects.isNull(e.getMessage()) ? "importExcelData.is.fail":e.getMessage());
        }

    }


}

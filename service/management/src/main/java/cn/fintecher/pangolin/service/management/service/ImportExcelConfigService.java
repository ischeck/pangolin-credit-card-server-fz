package cn.fintecher.pangolin.service.management.service;

import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.utils.InnerServiceUrl;
import cn.fintecher.pangolin.common.utils.SaxParseExcelUtil;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.managentment.ImportExcelConfig;
import cn.fintecher.pangolin.entity.managentment.QImportExcelConfig;
import cn.fintecher.pangolin.service.management.model.request.ImportExcelConfigParseRequest;
import cn.fintecher.pangolin.service.management.model.request.ImportExcelConfigRequest;
import cn.fintecher.pangolin.service.management.model.response.ImportExcelConfigParseResponse;
import cn.fintecher.pangolin.service.management.repository.ImportExcelConfigRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * @Author:peishouwen
 * @Desc: Excel导入模板配置
 * @Date:Create in 18:35 2018/7/23
 */
@Service("importExcelConfigService")
public class ImportExcelConfigService {
    Logger logger= LoggerFactory.getLogger(ImportExcelConfigService.class);

    @Autowired
    ImportExcelConfigRepository importExcelConfigRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    RestTemplate restTemplate;
    /**
     * 检查模板名称是否存在
     * @param name 模板名称
     * @param principalId 委托方ID
     * @return
     */
    public boolean checkNameIsExit(String name, String principalId ){
       return importExcelConfigRepository.exists(QImportExcelConfig.importExcelConfig.name.eq(name).
               and(QImportExcelConfig.importExcelConfig.principalId.eq(principalId)));
    }

    /**
     * 解析模板头文件
     * @param importExcelConfigParseRequest
     * @throws Exception
     */
    public List<ImportExcelConfigParseResponse> parserTemplateHeader(ImportExcelConfigParseRequest importExcelConfigParseRequest) throws Exception {
        List<ImportExcelConfigParseResponse> importExcelConfigParseResponseList=new ArrayList<>();
            String fileId=importExcelConfigParseRequest.getFileId();
            HttpHeaders headers = new HttpHeaders();
            ResponseEntity<byte[]> response = restTemplate.exchange(InnerServiceUrl.COMMON_SERVICE_GETFILEBYID.concat("/").concat(fileId),
                    HttpMethod.GET, new HttpEntity<byte[]>(headers), byte[].class);
            List<String> content=response.getHeaders().get("Content-Disposition");
            if(Objects.isNull(content) || content.isEmpty()){
                throw new Exception("templateFile.is.illegal");
            }
            if(!content.get(0).endsWith(SaxParseExcelUtil.EXCEL_TYPE_XLSX)){
                logger.error("fileName: {} ",content.get(0));
                throw  new Exception("file.format.error");
            }
            byte[] result = response.getBody();
            InputStream inputStream=new ByteArrayInputStream(result);
            Map<Integer,List<Map<String,String>>> dataMap=SaxParseExcelUtil.parseExcel(inputStream,importExcelConfigParseRequest.getTitleStartRow(),
                                         importExcelConfigParseRequest.getTitleStartCol(),1,importExcelConfigParseRequest.getSheetTotals());
            for(Map.Entry<Integer,List<Map<String,String>>> map :dataMap.entrySet()){
                List<Map<String,String>> titleRow=map.getValue();
                for(Map<String,String> cellMap :titleRow){
                    for(Map.Entry<String,String> entry :cellMap.entrySet()){
                        ImportExcelConfigParseResponse obj=new ImportExcelConfigParseResponse();
                        obj.setTitleName(entry.getValue());
                        obj.setCol(entry.getKey());
                        obj.setSheetNum(map.getKey());
                        importExcelConfigParseResponseList.add(obj);
                    }
                }
            }
            //排序
        importExcelConfigParseResponseList.sort(Comparator.comparing(obj -> SaxParseExcelUtil.excelColStrToNum(obj.getCol(), obj.getCol().length())));
        return  importExcelConfigParseResponseList;
    }

    /**
     * 保存模板配置
     * @param createTemplateDataInfoRequest
     */
    public void createImportExcelConfig(ImportExcelConfigRequest createTemplateDataInfoRequest, OperatorModel operatorModel){
        ImportExcelConfig importExcelConfig=new ImportExcelConfig();
        ModelMapper modelMapper=new ModelMapper();
        modelMapper.addMappings(new PropertyMap<ImportExcelConfigRequest, ImportExcelConfig>() {
            protected void configure() {
                skip().setId(null);
            }});
        modelMapper.map(createTemplateDataInfoRequest,importExcelConfig);
        importExcelConfig.setOperator(operatorModel.getFullName());
        importExcelConfig.setCreateTime(ZWDateUtil.getNowDateTime());
        importExcelConfigRepository.save(importExcelConfig);

    }

}

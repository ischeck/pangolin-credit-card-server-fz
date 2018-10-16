package cn.fintecher.pangolin.service.management.service;


import cn.fintecher.pangolin.entity.managentment.*;
import cn.fintecher.pangolin.service.management.repository.ContactResultRepository;
import cn.fintecher.pangolin.service.management.repository.ImportExcelConfigRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Desc: BBG
 * @Date:Create 2018/8/31
 */
@Service("exportConfigService")
public class ExportConfigService {

    @Autowired
    ImportExcelConfigRepository importExcelConfigRepository;
    @Autowired
    ContactResultRepository contactResultRepository;

    /**
     * 获取导入关联主键
     *
     * @param princialId
     * @return
     */
    public List<ExportConfigItem> getImportKeys(String princialId) {
        List<ExportConfigItem> exportConfigItems = new ArrayList<>();
        List<ImportExcelConfig> importExcelConfigs = IterableUtils.toList(importExcelConfigRepository.findAll(QImportExcelConfig.importExcelConfig.principalId.eq(princialId)));
        if (Objects.nonNull(importExcelConfigs) && importExcelConfigs.size() != 0) {
            for (ImportExcelConfig importExcelConfig : importExcelConfigs) {
                if (Objects.nonNull(importExcelConfig.getItems()) && importExcelConfig.getItems().size() != 0) {
                    importExcelConfig.getItems().forEach(e -> {
                        if (Objects.equals(e.isKeyFlag(), true)) {
                            boolean b = exportConfigItems.stream().anyMatch(u -> u.getAttribute().equals(e.getAttribute()));
                            if (!b) {
                                ExportConfigItem exportConfigItem = new ExportConfigItem();
                                BeanUtils.copyProperties(e, exportConfigItem);
                                exportConfigItem.setSource("BaseCase");
                                exportConfigItems.add(exportConfigItem);
                            }
                        }
                    });
                }
            }
        }
        return exportConfigItems;
    }


    /**
     * 获取主案件关键信息
     * @return
     */
    public List<ExportConfigItem> getBaseCaseKeys() {
        List<ExportConfigItem> exportConfigItems = new ArrayList<>();
        ExportConfigItem caseNumber = new ExportConfigItem();
        caseNumber.setSource("Case");
        caseNumber.setAttribute("caseNumber");
        caseNumber.setName("案件编号");
        exportConfigItems.add(caseNumber);
        ExportConfigItem account = new ExportConfigItem();
        account.setSource("Case");
        account.setAttribute("account");
        account.setName("账号");
        exportConfigItems.add(account);
        ExportConfigItem collector = new ExportConfigItem();
        collector.setSource("CaseFollowupRecord");
        collector.setAttribute("operatorName");
        collector.setName("催收员");
        ExportConfigItem visitor = new ExportConfigItem();
        visitor.setSource("CaseFollowupRecord");
        visitor.setAttribute("visitors");
        visitor.setName("外访员");
        ExportConfigItem followTime = new ExportConfigItem();
        followTime.setSource("CaseFollowupRecord");
        followTime.setAttribute("followTime");
        followTime.setName("跟进时间");
        exportConfigItems.add(followTime);
        ExportConfigItem personalName = new ExportConfigItem();
        personalName.setSource("Personal");
        personalName.setAttribute("personalName");
        personalName.setName("客户姓名");
        exportConfigItems.add(personalName);
        ExportConfigItem certificateNo = new ExportConfigItem();
        certificateNo.setSource("Personal");
        certificateNo.setAttribute("certificateNo");
        certificateNo.setName("证件号码");
        exportConfigItems.add(certificateNo);
        return exportConfigItems;
    }

    public List<ExportConfigItem> getAllConfigItems(String princialId) {
        List<ExportConfigItem> exportConfigItems = new ArrayList<>();
        List<ContactResult> contactResults = IterableUtils.toList(contactResultRepository.findAll(QContactResult.contactResult.principalId.eq(princialId)
            .and(QContactResult.contactResult.attribute.isNotNull())
            .and(QContactResult.contactResult.level.eq(2))));
        contactResults.forEach(contactResult -> {
            ExportConfigItem exportConfigItem = new ExportConfigItem();
            BeanUtils.copyProperties(contactResult, exportConfigItem);
            exportConfigItem.setSource("CaseFollowupRecord");
            exportConfigItems.add(exportConfigItem);
        });
        exportConfigItems.addAll(getBaseCaseKeys());
        return exportConfigItems;
    }

}

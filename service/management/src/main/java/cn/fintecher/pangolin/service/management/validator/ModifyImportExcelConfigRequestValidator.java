package cn.fintecher.pangolin.service.management.validator;

import cn.fintecher.pangolin.service.management.model.request.ImportExcelConfigRequest;
import cn.fintecher.pangolin.service.management.service.ImportExcelConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Locale;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 18:56 2018/7/23
 */
@Component("modifyImportExcelConfigRequestValidator")
public class ModifyImportExcelConfigRequestValidator implements Validator {

    @Autowired
    ImportExcelConfigService importExcelConfigService;

    @Autowired
    private MessageSource messageSource;


    @Override
    public boolean supports(Class<?> aClass) {
        return ModifyImportExcelConfigRequestValidator.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        if (errors.hasErrors()) {
            return;
        }
        ImportExcelConfigRequest object=(ImportExcelConfigRequest) o;
        if(importExcelConfigService.checkNameIsExit(object.getName(),object.getPrincipalId())){
            errors.reject("exist", messageSource.getMessage("importExcelConfigName.is.exist", null, Locale.CHINA));
        }

    }
}

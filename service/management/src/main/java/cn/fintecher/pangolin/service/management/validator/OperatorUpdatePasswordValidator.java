package cn.fintecher.pangolin.service.management.validator;

import cn.fintecher.pangolin.service.management.model.request.UpdateRequestPassword;
import cn.fintecher.pangolin.service.management.service.OperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Locale;
import java.util.Objects;

/**
 * Created by ChenChang on 2018/6/8.
 */
@Component
public class OperatorUpdatePasswordValidator implements Validator {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private OperatorService operatorService;

    @Override
    public boolean supports(Class<?> clazz) {
        return UpdateRequestPassword.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (errors.hasErrors()) {
            return;
        }
        UpdateRequestPassword request = (UpdateRequestPassword) target;
        if (Objects.isNull(request.getOldPassword())) {
            errors.reject("empty", messageSource.getMessage("oldPassword.is.empty", null, Locale.CHINA));
        }
        if (Objects.isNull(request.getNewPassword())) {
            errors.reject("empty", messageSource.getMessage("newPassword.is.empty", null, Locale.CHINA));
        }
        if (request.getOldPassword().equals(request.getNewPassword())) {
            errors.reject("empty", messageSource.getMessage("oldPassword.is.equals.newPassword", null, Locale.CHINA));
        }
    }
}

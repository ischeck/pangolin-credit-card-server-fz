package cn.fintecher.pangolin.service.management.validator;

import cn.fintecher.pangolin.service.management.model.request.CreateOperatorRequest;
import cn.fintecher.pangolin.service.management.repository.OrganizationRepository;
import cn.fintecher.pangolin.service.management.repository.RoleRepository;
import cn.fintecher.pangolin.service.management.service.OperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Locale;

/**
 * Created by ChenChang on 2018/6/8.
 */
@Component
public class OperatorCreateRequestValidator implements Validator {
    @Autowired
    private OperatorService operatorService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private MessageSource messageSource;

    @Override
    public boolean supports(Class<?> clazz) {
        return CreateOperatorRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (errors.hasErrors()) {
            return;
        }
        CreateOperatorRequest request = (CreateOperatorRequest) target;
        if (operatorService.checkUsername(request.getUsername())) {
            errors.reject("exist", messageSource.getMessage("operator.is.exist", null, Locale.CHINA));
        }
        if (!organizationRepository.existsById(request.getOrganization())) {
            errors.reject("org", messageSource.getMessage("org.not.exist", null, Locale.CHINA));
        }
    }
}

package cn.fintecher.pangolin.service.management.validator;

import cn.fintecher.pangolin.service.management.model.request.ModifyOperatorRequest;
import cn.fintecher.pangolin.service.management.repository.OperatorRepository;
import cn.fintecher.pangolin.service.management.repository.OrganizationRepository;
import cn.fintecher.pangolin.service.management.repository.RoleRepository;
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
public class OperatorModifyRequestValidator implements Validator {
    @Autowired
    private OperatorRepository operatorRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private MessageSource messageSource;
    @Override
    public boolean supports(Class<?> clazz) {
        return ModifyOperatorRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (errors.hasErrors()) {
            return;
        }
        ModifyOperatorRequest request = (ModifyOperatorRequest) target;

        if (!operatorRepository.existsById(request.getId())) {
            errors.reject("id", messageSource.getMessage("operator.not.exist", null, Locale.CHINA));
        }
        if (!organizationRepository.existsById(request.getOrganization())) {
            errors.reject("org", messageSource.getMessage("org.not.exist", null, Locale.CHINA));
        }
    }
}

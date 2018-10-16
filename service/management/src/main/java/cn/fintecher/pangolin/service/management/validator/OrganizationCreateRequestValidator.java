package cn.fintecher.pangolin.service.management.validator;

import cn.fintecher.pangolin.service.management.model.request.CreateOrganizationRequest;
import cn.fintecher.pangolin.service.management.repository.OrganizationRepository;
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
public class OrganizationCreateRequestValidator implements Validator {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private MessageSource messageSource;
    @Override
    public boolean supports(Class<?> clazz) {
        return CreateOrganizationRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (errors.hasErrors()) {
            return;
        }
        CreateOrganizationRequest request = (CreateOrganizationRequest) target;

        if (!organizationRepository.existsById(request.getParent())) {
            errors.reject("parent", messageSource.getMessage("org.parent.not.exist", null, Locale.CHINA));
        }


    }
}

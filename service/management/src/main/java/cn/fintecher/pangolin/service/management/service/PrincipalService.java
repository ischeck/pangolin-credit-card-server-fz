package cn.fintecher.pangolin.service.management.service;


import cn.fintecher.pangolin.common.enums.ConfigState;
import cn.fintecher.pangolin.common.enums.ManagementType;
import cn.fintecher.pangolin.common.enums.PrincipalState;
import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.utils.BeanUtils;
import cn.fintecher.pangolin.entity.managentment.ContactResult;
import cn.fintecher.pangolin.entity.managentment.Principal;
import cn.fintecher.pangolin.entity.managentment.QContactResult;
import cn.fintecher.pangolin.entity.managentment.QPrincipal;
import cn.fintecher.pangolin.service.management.model.request.CreatePrincipalRequest;
import cn.fintecher.pangolin.service.management.repository.ContactResultRepository;
import cn.fintecher.pangolin.service.management.repository.PrincipalRepository;
import org.apache.commons.collections4.IteratorUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by ChenChang on 2017/12/20.
 */
@Service("principalService")
public class PrincipalService {
    final Logger log = LoggerFactory.getLogger(PrincipalService.class);
    @Autowired
    private PrincipalRepository principalRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private OperatorService operatorService;
    @Autowired
    private ContactResultRepository contactResultRepository;

    /**
     * 新增委托方
     * @param token
     * @param createPrincipalRequest
     * @return
     */
    public Principal createPrincipal(String token, CreatePrincipalRequest createPrincipalRequest) {
        OperatorModel operator = operatorService.getSessionByToken(token);
        if (Objects.isNull(operator)) {
            throw new BadRequestException(null, "login", "operator.not.login");
        }
        //验证委托方名称是否存在
        checkPrincipal(createPrincipalRequest.getPrincipalName());
        Principal principal = new Principal();
        BeanUtils.copyProperties(createPrincipalRequest, principal);
        principal.setUser(operator.getId());
        principal.setState(PrincipalState.ENABLED);
        principal.setOperatorTime(new Date());
        Principal result = principalRepository.save(principal);
        return result;
    }

    /***
     * 生成联络结果
     * @param principal
     */
    public void generalConfigResult(Principal principal){
        Iterable<ContactResult> all = contactResultRepository.findAll(QContactResult.contactResult.level.eq(0));
        if(all.iterator().hasNext()){
            List<ContactResult> contactResults = IteratorUtils.toList(all.iterator());
            ContactResult contactResult = contactResults.get(0);
            ContactResult contactResultNew = new ContactResult();
            contactResultNew.setPid(contactResult.getId());
            contactResultNew.setLevel(contactResult.getLevel()+1);
            contactResultNew.setPrincipalId(principal.getId());
            contactResultNew.setPrincipalName(principal.getPrincipalName());
            contactResultNew.setIsExtension(ManagementType.YES);
            contactResultNew.setName(principal.getPrincipalName());
            contactResultNew.setConfigState(ConfigState.ENABLED);
            contactResultNew = contactResultRepository.save(contactResultNew);
            Iterable<ContactResult> all1 = contactResultRepository.findAll(QContactResult.contactResult.configState.eq(ConfigState.DISABLED));
            if(all1.iterator().hasNext()){
                List<ContactResult> contactResults1 = IteratorUtils.toList(all1.iterator());
                List<ContactResult> newList = new ArrayList<>();
                if(contactResults1.size()>0){
                    for(ContactResult contactResult1 : contactResults1){
                        ContactResult contactResult2 = new ContactResult();
                        BeanUtils.copyProperties(contactResult1, contactResult2);
                        contactResult2.setId(null);
                        contactResult2.setPid(contactResultNew.getId());
                        contactResult2.setPrincipalName(contactResultNew.getPrincipalName());
                        contactResult2.setPrincipalId(contactResultNew.getPrincipalId());
                        contactResult2.setConfigState(ConfigState.ENABLED);
                        newList.add(contactResult2);
                    }
                }
                contactResultRepository.saveAll(newList);
            }
        }
    }

    /**
     * 检查是否存在该委托方
     *
     * @param principalName
     * @return
     */
    public void checkPrincipal(String principalName) {
        QPrincipal qPrincipal = QPrincipal.principal;
        Boolean exists = principalRepository.exists(qPrincipal.principalName.eq(principalName));
        if (exists) {
            throw new BadRequestException(null, "principle", "principal.name.is.exist");
        }
    }
}
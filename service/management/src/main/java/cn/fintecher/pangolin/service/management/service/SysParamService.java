package cn.fintecher.pangolin.service.management.service;

import cn.fintecher.pangolin.entity.managentment.QSysParam;
import cn.fintecher.pangolin.entity.managentment.SysParam;
import cn.fintecher.pangolin.service.management.repository.SystemParamRepository;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @Author:peishouwen
 * @Desc:
 * @Date:Create in 16:45 2018/9/17
 */
@Service("sysParamService")
public class SysParamService {

    @Autowired
    SystemParamRepository systemParamRepository;

    /**
     * 根据条件查询需要参数
     * @param sysParam
     * @return
     */
    public SysParam findOne(SysParam sysParam){
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QSysParam qSysParam=QSysParam.sysParam;
        if(!StringUtils.isBlank(sysParam.getId())){
            booleanBuilder.and(qSysParam.id.eq(sysParam.getId()));
        }
        if(!StringUtils.isBlank(sysParam.getCode())){
            booleanBuilder.and(qSysParam.code.eq(sysParam.getCode()));
        }
        Optional<SysParam> result= systemParamRepository.findOne(booleanBuilder);
        if(result.isPresent()){
            return  result.get();
        }else {
            return null;
        }
    }
}

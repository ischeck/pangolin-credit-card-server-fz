package cn.fintecher.pangolin.service.common.web;


import cn.fintecher.pangolin.common.enums.EnumMessage;
import cn.fintecher.pangolin.common.model.DataDict;
import cn.fintecher.pangolin.common.web.BaseController;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.util.*;

/**
 * Created by ChenChang on 2017/10/11.
 */
@RestController
@RequestMapping("/api/dataDict")
@Api(value = "数据字典相关", description = "数据字典相关")
public class DataDictController extends BaseController {
    private final Logger log = LoggerFactory.getLogger(DataDictController.class);
    @Autowired
    private MessageSource messageSource;

    @ApiOperation(value = "获取数据字典", notes = "获取数据字典")
    @GetMapping("/getAll")
    public ResponseEntity<Map<String, Collection<DataDict>>> getAll(WebRequest request) throws ClassNotFoundException {
        log.debug("获取数据字典项目 local为：{}", request.getLocale());
        Multimap<String, DataDict> dictMultimap = ArrayListMultimap.create();
        List<Class<?>> list = new ArrayList<>();
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.AddressType"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.AddressStatus"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.ApplyFileContent"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.ApplyType"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.ApprovalResult"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.ApprovalStatus"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.AreaType"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.AssistApprovedResult"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.AssistApprovedStatus"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.AssistFlag"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.AssistStatus"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.AssistType"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.AttributeType"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.BusinessType"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.CaseDataStatus"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.CaseIssuedFlag"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.CertificateType"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.CollConfigType"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.CollectionType"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.CommentType"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.CustConfigType"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.DataState"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.DistributeWay"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.FindType"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.FollowType"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.ImportDataExcelStatus"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.LabelType"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.ManagementType"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.Marital"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.OperatorState"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.OrganizationType"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.OtherFollowType"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.PaymentStatus"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.PaymentType"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.PayStatus"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.PhoneStatus"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.PhoneType"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.PrincipalState"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.PrincipalType"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.PublicCaseStatus"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.Relationship"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.ResourceType"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.RoleState"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.Sex"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.SocialNetwork"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.StrategyState"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.StrategyType"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.Symbol"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.SysParamDisplayType"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.SysParamState"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.SysParamStyle"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.Target"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.TemplateType"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.OtherTemplateType"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.CaseLeaveFlag"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.Source"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.CollectionOrganization"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.ExportState"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.ClockStatus"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.ClockType"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.OrganizationApproveType"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.UserState"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.GroupType"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.MessageMode"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.MessageReadStatus"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.MessageType"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.TaskBoxStatus"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.TaskBoxType"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.NoticeType"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.ExportType"));
        list.add(Class.forName("cn.fintecher.pangolin.common.enums.SensitiveLevel"));




//        List<Class<EnumMessage>> list = InterfaceHelp.getDataClass("cn.fintecher.pangolin.common.enums", EnumMessage.class);
        for (Class<?> l : list) {

            Class c = Class.forName(l.getName());
            if (Objects.isNull(c.getEnumConstants())) {
                continue;
            }
            for (Object obj : c.getEnumConstants()) {
                DataDict dataDict = new DataDict(((Enum<?>) obj).name(), messageSource.getMessage(((EnumMessage) obj).getMessageKey((Enum<?>) obj), null, Locale.CHINA));
                dictMultimap.put(c.getSimpleName(), dataDict);

            }
        }
        return ResponseEntity.ok().body(dictMultimap.asMap());
    }

}

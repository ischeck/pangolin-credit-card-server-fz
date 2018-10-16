package cn.fintecher.pangolin.common.utils;


/**
 * @Description: 内部服务调用URL
 * @Author: peishouwen
 * @Date Created in 2018/7/25 0:31
 */
public class InnerServiceUrl {
    /**
     * 通过文件ID获取文件流
     */
    public static  final String COMMON_SERVICE_GETFILEBYID="http://common-service/api/uploadFile/getFileStreamById";

    /**
     * 获取token
     */
    public static  final String MANAGEMENT_SERVICE_GETBYTOKEN="http://management-service/api/operators/getUserByToken/";

    /**
     * 获取案件状态
     */
    public static  final String MANAGEMENT_SERVICE_CASESTATUS="http://management-service/api/custConfig/getCaseStateByPrin?principalId=";


    /**
     * 获取用户组织对应下分公司的所有组织
     */
    public static  final String MANAGEMENT_SERVICE_FINDOPERATORORGANIZATION="http://management-service/api/organization/findOperatorOrganization?orgId=";

    /**
     * 获取用户组织对应的所有父类
     */
    public static  final String MANAGEMENT_SERVICE_FIND_PARENT_ID="http://management-service/api/organization/findParentId?orgId=";

    /**
     * 获取token
     */
    public static  final String MANAGEMENT_SERVICE_FIND_EXPORT_ITEM="http://management-service/api/exportConfigController/getFollowConfigItems?principalId=";

    /**
     * 查询配置流程
     */
    public static  final String MANAGEMENT_SERVICE_FINDCONFIGFLOW="http://management-service/api/configFlowController/findConfigFlow";

    /**
     * 通过ID获取模板配置
     */
    public static  final String MANAGEMENT_SERVICE_GETTEMPLATEBYID ="http://management-service/api/templateDataController/getTemplateData/";

    public static final String COMMON_SERVICE_UPLOADFILE="http://common-service/api/uploadFile/upload";

    /**
     * 通过催收员ID查询催收案件
     */
    public static final String DOMIAN_SERVICE_SEARCHBASECASE="http://domain-service/api/collectionCase/searchBaseCase?collector=";

    /**
     * 历史案件转移
     */
    public static final String DOMIAN_SERVICE_HISCASETRANS="http://domain-service/api/collectionCase/hisCaseTrans";


    /**
     * 备忘录提醒
     */
    public static final String DATAIMP_SERVICE_COMMENTREMIND="http://dataimp-service/api/schedule/sendCommentMsg?minute=";

    /**
     * 案件记录处理
     */
    public static final String DOMIAN_SERVICE_CASERECORDHANDLE="http://domain-service/api/collectionCase/caseRecordHandle";

    /**
     * 获取各个委托方主键关联字段
     */
    public static final String MANAGEMENT_SERVICE_PRIMARY_PROPERTY="http://management-service/api/exportConfigController/getPrimaryProperty?principalId=";

    /**
     * 获取催记导出模板
     */
    public static final String MANAGEMENT_SERVICE_PRIMARY_EXPORTCONFIG = "http://management-service/api/exportConfigController/getTemplate?id=";

}

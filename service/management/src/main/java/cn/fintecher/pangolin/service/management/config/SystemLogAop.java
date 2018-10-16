package cn.fintecher.pangolin.service.management.config;

import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.entity.managentment.SystemLog;
import cn.fintecher.pangolin.service.management.repository.SystemLogRepository;
import cn.fintecher.pangolin.service.management.service.OperatorService;
import io.swagger.annotations.ApiOperation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * @Author: huyanmin
 * @Description: 系统日志
 * @Date 2018/6/29
 */
@Aspect
@Order(1)
@Component
public class SystemLogAop {

    private final static String AFTER = "after";
    private final static String EXCEPTION = "exception";
    private final Logger log = LoggerFactory.getLogger(SystemLogAop.class);
    ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Autowired
    private OperatorService operatorService;

    @Autowired
    private SystemLogRepository systemLogRepository;

    // 切入点*Controller
    @Pointcut("execution(public * cn.fintecher.pangolin.*.*.web.*Controller.*(..))")
    public void systemLogAop() {
    }

    @Before("systemLogAop()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        startTime.set(System.currentTimeMillis());
        log.info("进入的方法的时间戳" + System.currentTimeMillis());
    }

    @After("systemLogAop()")
    public void doAfter(JoinPoint joinPoint) throws Throwable {
        saveSystemLogsBlock(joinPoint, AFTER, null, startTime);
    }

    /**
     * 异常通知 用于拦截记录异常日志
     *
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(pointcut = "systemLogAop()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable e) {
        saveSystemLogsBlock(joinPoint, EXCEPTION, e, startTime);
    }

    private void saveSystemLogsBlock(JoinPoint joinPoint, String type, Throwable e, ThreadLocal<Long> startTime) {
        try {
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            Object[] obj = joinPoint.getArgs();
            if (null == obj || obj.length < 1) {
                log.info("Request Method: {}.{}", className, methodName);
            } else {
                log.info("Request Method: {}.{}, Request body : {}", className, methodName, obj);
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                String tokenStr = request.getHeader("X-UserToken");
                // 获取到请求地址
                String remoteAddr = getAddr(request);
                // 请求执行时间
                Long exeTime = System.currentTimeMillis() - startTime.get();
                if (tokenStr != null && !tokenStr.isEmpty()) {
                    String remark= getMethodRemark(className, methodName, obj, type, e);
                    OperatorModel sessionByToken = operatorService.getSessionByToken(tokenStr);
                    log.info("Request Method: {}.{}, Request user_id : {}", className, methodName, sessionByToken.getId());
                    SystemLog systemLogs = new SystemLog();
                    systemLogs.setClientIp(remoteAddr);
                    systemLogs.setOperator(sessionByToken.getUsername());
                    systemLogs.setOperateTime(new Date());
                    systemLogs.setRemark(remark);
                    systemLogs.setExeTime(exeTime.toString());
                    systemLogs.setExeMethod(methodName);
                    systemLogs.setExeType(type);
                    systemLogRepository.save(systemLogs);
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            log.debug(e1.getMessage());
        }
    }

    private String getMethodRemark(String className, String methodName, Object[] obj, String type, Throwable e){
        Class targetClass = null;
        try {
            targetClass = Class.forName(className);
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
        // 获取到所有公有方法
        Method[] methods = targetClass.getMethods();
        String remark = "";
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazz = method.getParameterTypes();
                // 方法名和参数数量相同认为是同一个方法
                if (clazz.length == obj.length) {
                    remark = method.getAnnotation(ApiOperation.class).value();
                    break;
                }
            }
        }
        if (EXCEPTION.equals(type)) {
            remark += e.getClass().getName() + e.getMessage();
        }
        return remark;
    }

    private static String getAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}

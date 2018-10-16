package cn.fintecher.pangolin.service.gateway.filter;


import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.http.HttpServletRequest;

/**
 * 针对系统的访问控制
 * Created by ChenChang on 2017/8/21.
 */
public class AccessFilter extends ZuulFilter {


    private static Logger log = LoggerFactory.getLogger(AccessFilter.class);

    @Autowired
    private RedisTemplate<String, String> jsonRedisTemplate;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    public boolean shouldFilter() {
        return true;
    }

    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        log.debug(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));
        String accessToken = request.getHeader("X-UserToken");
        if (StringUtils.contains(request.getRequestURL().toString(), "/api/operators/login")
                || StringUtils.contains(request.getRequestURL().toString(), "/api/appManegement/login")
                || StringUtils.contains(request.getRequestURL().toString(),"api/dataDict/getAll")
                || StringUtils.contains(request.getRequestURL().toString(), "swagger")
                || StringUtils.contains(request.getRequestURL().toString(), "api-docs")) {
            return null;
        }
        if (StringUtils.isBlank(accessToken)) {
            log.debug("access token is empty");
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(403);
            return null;
        }
        try {
            Boolean login = jsonRedisTemplate.hasKey(accessToken);
            if (!login) {
                log.debug("access token is wrong");
                ctx.setSendZuulResponse(false);
                ctx.setResponseStatusCode(403);
                return null;
            }

        } catch (Exception e) {
            log.debug("access token is wrong");
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(403);
            return null;
        }

        log.debug("access token ok");
        return null;
    }


}

package net.cz.blog.interceptor;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import net.cz.blog.Response.ResponseResult;
import net.cz.blog.utils.Constants;
import net.cz.blog.utils.CookieUtils;
import net.cz.blog.utils.RedisUtil;
import net.cz.blog.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.lang.reflect.Method;


@Slf4j
@Component
public class ApiInterceptor implements AsyncHandlerInterceptor {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private Gson gson;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            //不是所有的方法都要拦截 只拦截那些可能频繁提交的注解
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            String name = method.getName();
            checkTooFrequentCommit annotation = method.getAnnotation(checkTooFrequentCommit.class);
            if (annotation != null) {
                //判断是否提交太频繁
                //用token作为可以来记录提交频率
                String tokenKey = CookieUtils.getCookie(request, Constants.User.COOKIE_TOKEN_KEY);
                if (!TextUtils.isEmpty(tokenKey)) {
                    String hasCommit = (String) redisUtil.get(Constants.User.KEY_COMMIT_TOKEN_RECORD + tokenKey);
                    if (TextUtils.isEmpty(hasCommit)) {
                        //初次提交放行  10s无法再次提交
                        redisUtil.set(Constants.User.KEY_COMMIT_TOKEN_RECORD + tokenKey, "true",
                                Constants.TimeValue.SECOND_10);
                    } else {
                        //拦截 返回一个失败的结果
                        response.setCharacterEncoding("UTF-8");
                        response.setContentType("application/json");
                        ResponseResult failed = ResponseResult.FAILED("提交太过于频繁");
                        PrintWriter writer = response.getWriter();
                        writer.write(gson.toJson(failed));
                        writer.flush();
                        return false;
                    }
                }
            }
            log.info("name-->" + name);
        }

        //true 代表放行
        //false 代表拦截
        return true;
    }
}

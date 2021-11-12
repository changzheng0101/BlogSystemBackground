package net.cz.blog.services.Impl;


import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.BlogUser;
import net.cz.blog.services.IUserService;
import net.cz.blog.utils.Constants;
import net.cz.blog.utils.CookieUtils;
import net.cz.blog.utils.JwtUtil;
import net.cz.blog.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Service("permission")
public class PermissionService {


    @Autowired
    private IUserService userService;

    /**
     * 判断是不是管理员
     *
     * @return
     */
    public boolean isAdmin() {
        //拿到request和response
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();

        //判断有没有令牌 机器上没有令牌 肯定没有登录
        String tokenKey = CookieUtils.getCookie(request, Constants.User.COOKIE_TOKEN_KEY);
        if (TextUtils.isEmpty(tokenKey)) {
            return false;
        }
        //判断是否拥有权限
        BlogUser currentUser = userService.checkBolgUser();
        if (currentUser == null) {
            return false;
        }
        //判断角色
        if (Constants.User.ROLE_ADMIN.equals(currentUser.getRoles())) {
            return true;
        }
        return false;
    }
}

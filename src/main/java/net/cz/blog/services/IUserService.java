package net.cz.blog.services;

import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.BlogUser;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IUserService {
    public ResponseResult initManagerAccount(BlogUser user, HttpServletRequest request);

    void createCaptcha(HttpServletResponse response, String captcha_key) throws Exception;

    ResponseResult sendEmail(String type,String emailAddress, HttpServletRequest request) throws MessagingException;

    ResponseResult register(BlogUser user, String emailVerifyCode, String captcha,
                            String captchaKey, HttpServletRequest request);

    ResponseResult doLogin(String captcha, String captcha_key, BlogUser user,
                           HttpServletRequest request, HttpServletResponse response);


    BlogUser checkBolgUser(HttpServletRequest request, HttpServletResponse response);

    ResponseResult getUserInfo(String userId);

    ResponseResult checkEmail(String email);

    ResponseResult checkUserName(String userName);

    ResponseResult updateUserInfo(HttpServletRequest request, HttpServletResponse response,
                                  String userId, BlogUser user);

    ResponseResult deleteUser(HttpServletResponse response, HttpServletRequest request, String userId);

    ResponseResult getUserList(int page, int size, HttpServletRequest request, HttpServletResponse response);
}

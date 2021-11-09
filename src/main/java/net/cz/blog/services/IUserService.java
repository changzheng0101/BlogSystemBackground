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

}

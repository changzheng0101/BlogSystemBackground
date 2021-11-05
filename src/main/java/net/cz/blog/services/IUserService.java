package net.cz.blog.services;

import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.BlogUser;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IUserService {
    public ResponseResult initManagerAccount(BlogUser user, HttpServletRequest request);

    void createCaptcha(HttpServletResponse response, String captcha_key) throws Exception;

    ResponseResult sendEmail(String emailAddress, HttpServletRequest request) throws MessagingException;
}

package net.cz.blog.services;

import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.BlogUser;

import javax.servlet.http.HttpServletRequest;

public interface IUserService {
    public ResponseResult initManagerAccount(BlogUser user, HttpServletRequest request);
}

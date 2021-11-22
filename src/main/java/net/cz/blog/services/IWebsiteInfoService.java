package net.cz.blog.services;

import net.cz.blog.Response.ResponseResult;

public interface IWebsiteInfoService {
    ResponseResult getWebsiteViewCount();


    ResponseResult putSeoInfo(String description, String keywords);

    ResponseResult getSeoInfo();

    ResponseResult updateWebsiteTitle(String title);

    ResponseResult getWebsiteTitle();

}

package net.cz.blog.controller.admin;

import net.cz.blog.Response.ResponseResult;
import net.cz.blog.services.IWebsiteInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/web_info")
public class WebInfoAdminApi {

    @Autowired
    private IWebsiteInfoService websiteInfoService;

    @PreAuthorize("permission.isAdmin()")
    @GetMapping("/title")
    public ResponseResult getWebsiteTitle() {
        return websiteInfoService.getWebsiteTitle();
    }

    @PreAuthorize("permission.isAdmin()")
    @PutMapping("/title")
    public ResponseResult updateWebsiteTitle(@RequestParam("title") String title) {
        return websiteInfoService.updateWebsiteTitle(title);
    }

    //seo search engine optimization 搜索引擎优化
    //在自然搜索中提高自己的排名'
    @PreAuthorize("permission.isAdmin()")
    @GetMapping("/seo")
    public ResponseResult getSeoInfo() {
        return websiteInfoService.getSeoInfo();
    }

    @PreAuthorize("permission.isAdmin()")
    @PutMapping("/seo")
    public ResponseResult putSeoInfo(@RequestParam("keywords") String keywords,
                                     @RequestParam("description") String description) {
        return websiteInfoService.putSeoInfo(description, keywords);
    }

    //    获取后台统计信息
    @PreAuthorize("permission.isAdmin()")
    @GetMapping("/view_count")
    public ResponseResult getWebsiteViewCount() {
        return websiteInfoService.getWebsiteViewCount();
    }

}

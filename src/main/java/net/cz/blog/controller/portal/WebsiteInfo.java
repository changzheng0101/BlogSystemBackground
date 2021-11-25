package net.cz.blog.controller.portal;


import net.cz.blog.Response.ResponseResult;
import net.cz.blog.services.ICategoryService;
import net.cz.blog.services.IFriendLinkService;
import net.cz.blog.services.ILooperService;
import net.cz.blog.services.IWebsiteInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portal/website_info")
public class WebsiteInfo {
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private IWebsiteInfoService websiteInfoService;
    @Autowired
    private ILooperService looperService;
    @Autowired
    private IFriendLinkService friendLinkService;


    @GetMapping("/categories")
    public ResponseResult getCategories() {
        return categoryService.getCategoryList();
    }

    @GetMapping("/title")
    public ResponseResult getWebsiteTitle() {
        return websiteInfoService.getWebsiteTitle();
    }

    @GetMapping("/view_count")
    public ResponseResult getWebsiteViewCount() {
        return websiteInfoService.getWebsiteViewCount();
    }

    @GetMapping("/seo")
    public ResponseResult getWebsiteSeo() {
        return websiteInfoService.getSeoInfo();
    }

    @GetMapping("/loop")
    public ResponseResult getLoops() {
        return looperService.getLooperList();
    }

    @GetMapping("/friend_link")
    public ResponseResult getFriendLink() {
        return friendLinkService.getFriendLinkList();
    }
}

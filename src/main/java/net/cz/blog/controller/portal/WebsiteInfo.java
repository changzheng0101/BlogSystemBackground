package net.cz.blog.controller.portal;


import net.cz.blog.Response.ResponseResult;
import net.cz.blog.services.ICategoryService;
import net.cz.blog.services.IFriendLinkService;
import net.cz.blog.services.ILooperService;
import net.cz.blog.services.IWebsiteInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portal/website_info")
public class WebsiteInfo {

    @Autowired
    private IWebsiteInfoService websiteInfoService;
    @Autowired
    private ILooperService looperService;
    @Autowired
    private IFriendLinkService friendLinkService;




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

    /**
     * 统计访问量
     * 1.每个页面都会增加访问量
     * 2.应该按照ip过滤
     * 3.可以集成第三方工具进行统计
     * <p>
     * 统计信息保存在redis中，同时mysql中也有保存
     * 当用户获取统计信息的时候，才从mysql中进行获取
     * <p>
     * redis：每个页面访问时，没有就去mysql中拿，有就自增
     * mysql：用户访问总访问量的时候
     */
    @PutMapping("/view_count")
    public void updateViewCount() {
        websiteInfoService.updateViewCount();
    }
}

package net.cz.blog.controller.portal;


import net.cz.blog.Response.ResponseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portal/website_info")
public class WebsiteInfo {
    @GetMapping("/categories")
    public ResponseResult getCategories() {
        return null;
    }

    @GetMapping("/title")
    public ResponseResult getWebsiteTitle() {
        return null;
    }

    @GetMapping("/view_count")
    public ResponseResult getWebsiteViewCount() {
        return null;
    }

    @GetMapping("/seo")
    public ResponseResult getWebsiteSeo() {
        return null;
    }

    @GetMapping("/loop")
    public ResponseResult getLoops() {
        return null;
    }

    @GetMapping("/friend_link")
    public ResponseResult getFriendLink() {
        return null;
    }
}

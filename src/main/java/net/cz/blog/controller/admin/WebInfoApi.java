package net.cz.blog.controller.admin;

import net.cz.blog.Response.ResponseResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/web_info")
public class WebInfoApi {
    @GetMapping("/title")
    public ResponseResult getWebsiteTitle() {
        return null;
    }

    @PutMapping("/title")
    public ResponseResult updateWebsiteTitle(@RequestParam("title") String title) {
        return null;
    }

    @GetMapping("/seo")
    public ResponseResult getSeoInfo() {
        return null;
    }

    @PutMapping("/seo")
    public ResponseResult putSeoInfo(@RequestParam("keywords") String keywords,
                                     @RequestParam("description") String description) {
        return null;
    }

    //    获取后台统计信息
    @GetMapping("/view_count")
    public ResponseResult getWebsiteViewCount() {
        return null;
    }
}

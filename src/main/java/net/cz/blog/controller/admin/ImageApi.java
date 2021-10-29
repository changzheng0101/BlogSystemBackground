package net.cz.blog.controller.admin;

import net.cz.blog.Response.ResponseResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/image")
public class ImageApi {
    //增删改查 还有列出所有数据的方法
    @PostMapping
    public ResponseResult uploadImage() {
        return null;
    }

    @DeleteMapping("/{imageId}")
    public ResponseResult deleteImage(@PathVariable("imageId") String imageId) {
        return null;
    }

    @PutMapping("/{imageId}")
    public ResponseResult updateImage(@PathVariable("imageId") String imageId) {
        return null;
    }

    @GetMapping("/{imageId}")
    public ResponseResult getImage(@PathVariable("imageId") String imageId) {
        return null;
    }

    @GetMapping("/list")
    public ResponseResult getImageList(@RequestParam("page") int page, @RequestParam("size") int size) {
        return null;
    }

}

package net.cz.blog.controller.admin;

import net.cz.blog.Response.ResponseResult;
import net.cz.blog.services.IImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/admin/image")
public class ImageAdminApi {
    //增删改查 还有列出所有数据的方法

    @Autowired
    private IImageService imageService;

    /**
     * 图片上传的方法
     * 对象存储-->很简单，看文档就行，但是需要购买华为云等设施
     * 使用 Nginx和fastDFX Nginx用于处理文件访问 fastDFX用于处理文件上传 复杂
     * <p>
     * 该项目使用的是文件操作
     *
     * @param file
     * @return
     */
    @PreAuthorize("@permission.isAdmin()")
    @PostMapping
    public ResponseResult uploadImage(@RequestParam("file") MultipartFile file) {
        return imageService.uploadImage(file);
    }

    @PreAuthorize("@permission.isAdmin()")
    @DeleteMapping("/{imageId}")
    public ResponseResult deleteImage(@PathVariable("imageId") String imageId) {
        return null;
    }

    @PreAuthorize("@permission.isAdmin()")
    @GetMapping("/{imageId}")
    public void getImage(@PathVariable("imageId") String imageId, HttpServletResponse response) throws IOException {
        imageService.getImage(imageId, response);
    }

    @PreAuthorize("@permission.isAdmin()")
    @GetMapping("/list")
    public ResponseResult getImageList(@RequestParam("page") int page, @RequestParam("size") int size) {
        return null;
    }

}

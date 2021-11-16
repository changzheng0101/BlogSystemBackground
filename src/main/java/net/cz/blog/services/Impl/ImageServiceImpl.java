package net.cz.blog.services.Impl;

import lombok.extern.slf4j.Slf4j;
import net.cz.blog.Response.ResponseResult;
import net.cz.blog.services.IImageService;
import net.cz.blog.utils.TextUtils;
import org.springframework.data.repository.core.support.ReactiveRepositoryFactorySupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
@Transactional
public class ImageServiceImpl implements IImageService {

    private final String imagePath = "D:\\Code\\WEBProject\\images";

    @Override
    public ResponseResult uploadImage(MultipartFile file) {
        //判断文件是否为空
        if (file == null) {
            return ResponseResult.FAILED("文件不可以为空");
        }
        // 判断文件类型 只支持 jpg gif png jpeg
        String contentType = file.getContentType();
        if (TextUtils.isEmpty(contentType)) {
            return ResponseResult.FAILED("图片格式错误");
        }
        if (!"image/png".equals(contentType) &&
                !"image/jpg".equals(contentType) &&
                !"image/gif".equals(contentType) &&
                !"image/jpeg".equals(contentType)) {
            return ResponseResult.FAILED("图片格式不支持");
        }
        // 获取相关数据 比如文件数据 文件名称
        String name = file.getName();
        String originalFilename = file.getOriginalFilename();
        log.info("name--->" + name); //无后缀
        log.info("originalFilename--->" + originalFilename); //有后缀
        // 根据我们的规则进行命名
        File targetFile = new File(imagePath + File.separator + originalFilename);
        // 保存数据
        try {
            file.transferTo(targetFile);
        } catch (IOException e) {
            e.printStackTrace();
            ResponseResult.FAILED("图片保存失败");
        }
        return ResponseResult.SUCCESS("图片保存成功");
    }
}

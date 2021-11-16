package net.cz.blog.services;

import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.FriendLink;
import org.springframework.web.multipart.MultipartFile;

public interface IImageService {
    ResponseResult uploadImage(MultipartFile file);
}

package net.cz.blog.controller;


import net.cz.blog.Response.ResponseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 错误码转换
 */
@RestController
public class ErrorPageController {

    @GetMapping("/403")
    public ResponseResult page403() {
        return ResponseResult.Error_403();
    }

    @GetMapping("/404")
    public ResponseResult page404() {
        return ResponseResult.Error_404();
    }

    @GetMapping("/504")
    public ResponseResult page504() {
        return ResponseResult.Error_504();
    }

    @GetMapping("/505")
    public ResponseResult page505() {
        return ResponseResult.Error_505();
    }
}

package net.cz.blog.controller.user;


import lombok.extern.slf4j.Slf4j;
import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.BlogUser;
import net.cz.blog.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserApi {

    @Autowired
    private IUserService userService;

    //   初始化管理员账号
    @PostMapping("/admin_account")
    public ResponseResult initManagerAccount(@RequestBody BlogUser user, HttpServletRequest request) {
        log.info("user name==>" + user.getUser_name());
        log.info("password==>" + user.getPassword());
        log.info("email ==> " + user.getEmail());

        return userService.initManagerAccount(user, request);
    }

    //注册
    @PostMapping
    public ResponseResult register(@RequestBody BlogUser user) {
        return null;
    }

    //登录   加验证码  测试是否是人类
    @PostMapping("/{captcha}")
    public ResponseResult login(@PathVariable("captcha") String captcha, @RequestBody BlogUser user) {
        return null;
    }

    //获取图灵验证码
    @GetMapping("/captcha")
    public ResponseResult getCaptcha() {
        return null;
    }

    //发送邮件
    @GetMapping("/verify_code")
    public ResponseResult sendVerifyCode(@RequestParam("email") String email) {
        return null;
    }

    //修改密码 put请求用于更新
    @PutMapping("/password/{userId}")
    public ResponseResult updatePassword(@PathVariable("userId") String userId, @RequestBody BlogUser user) {
        return null;
    }

    //获取用户信息
    @GetMapping("/{userId}")
    public ResponseResult getUserInfo(@PathVariable("userId") String userId) {
        return null;
    }

    //更新用户
    @PutMapping("/{userId}")
    public ResponseResult updateUserInfo(@PathVariable("userId") String userId, @RequestBody BlogUser user) {
        return null;
    }

    //查看用户
    @GetMapping("/list")
    public ResponseResult getUserList(@RequestParam("page") int page, @RequestParam("size") int size) {
        return null;
    }

    //删除用户
    @DeleteMapping("/{userId}")
    public ResponseResult deleteUser(@PathVariable("userId") String userId) {
        return null;
    }
}

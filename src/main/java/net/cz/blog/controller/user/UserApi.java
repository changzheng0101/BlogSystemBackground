package net.cz.blog.controller.user;


import lombok.extern.slf4j.Slf4j;
import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.BlogUser;
import net.cz.blog.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserApi {

    @Autowired
    private IUserService userService;

    //   初始化管理员账号
    @PostMapping("/admin_account")
    public ResponseResult initManagerAccount(@RequestBody BlogUser user, HttpServletRequest request) {
        log.info("user name==>" + user.getUserName());
        log.info("password==>" + user.getPassword());
        log.info("email ==> " + user.getEmail());

        return userService.initManagerAccount(user, request);
    }

    //注册
    @PostMapping
    public ResponseResult register(@RequestBody BlogUser user,
                                   @RequestParam("email_verify_code") String emailVerifyCode,
                                   @RequestParam("captcha") String captcha,
                                   @RequestParam("captcha_key") String captchaKey,
                                   HttpServletRequest request) {
        return userService.register(user, emailVerifyCode, captcha, captchaKey, request);
    }

    //登录   加验证码  测试是否是人类
    //需要提交的数据
    //1 用户账号或者邮箱 已经进行唯一处理
    //2 密码
    //3 图灵验证码
    //4 图灵验证码的key
    @PostMapping("/{captcha}/{captcha_key}")
    public ResponseResult login(@PathVariable("captcha_key") String captchaKey,
                                @PathVariable("captcha") String captcha,
                                @RequestBody BlogUser user,
                                HttpServletRequest request,
                                HttpServletResponse response) {

        return userService.doLogin(captcha, captchaKey, user, request, response);
    }


    //获取图灵验证码
    @GetMapping("/captcha")
    public void getCaptcha(HttpServletResponse response, @RequestParam("captcha_key") String captcha_key) {
        try {
            userService.createCaptcha(response, captcha_key);

        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    //发送邮件
    //业务场景 注册 找回密码 更换邮箱（会输入新的邮箱）
    //注册：如果已经注册，提示已经注册
    //找回密码：邮箱未注册的话，提示未注册
    //更换邮箱：查看新的邮箱是否已经注册
    //用一个type来区别这几种情况
    @GetMapping("/verify_code")
    public ResponseResult sendVerifyCode(HttpServletRequest request, @RequestParam("type") String type,
                                         @RequestParam("email") String emailAddress) {
        ResponseResult responseResult = ResponseResult.FAILED();
        try {
            responseResult = userService.sendEmail(type, emailAddress, request);

        } catch (Exception e) {
            log.error("sendVerifyCode==>" + e);
        }
        return responseResult;
    }

    //修改密码 put请求用于更新
    @PutMapping("/password/{userId}")
    public ResponseResult updatePassword(@PathVariable("userId") String userId, @RequestBody BlogUser user) {
        return null;
    }

    //获取用户信息
    @GetMapping("/{userId}")
    public ResponseResult getUserInfo(@PathVariable("userId") String userId) {
        return userService.getUserInfo(userId);
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

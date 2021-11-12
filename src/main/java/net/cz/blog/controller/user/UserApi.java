package net.cz.blog.controller.user;


import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.BlogUser;
import net.cz.blog.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PostMapping("/join_in")
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
    @PostMapping("/login/{captcha}/{captcha_key}")
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


    /**
     * 修改密码：
     * 用户已经登录的情况下，进行修改即可，完事退出登录，让用户重新登录
     * <p>
     * 找回密码：
     * 步骤：
     * 1、用户填写邮箱
     * 2、用户获取验证码verifyCode
     * 3、用户填写验证码
     * 4、用户填写新的密码
     * 5、提交数据
     * <p>
     * 数据包括：
     * 邮箱和验证码
     * 新密码
     *
     * @param verifyCode
     * @param user
     * @return
     */
    @PutMapping("/password/{verifyCode}")
    public ResponseResult updatePassword(@PathVariable("verifyCode") String verifyCode, @RequestBody BlogUser user) {
        return userService.updatePassword(verifyCode, user);
    }

    //获取用户信息
    @GetMapping("/user_info/{userId}")
    public ResponseResult getUserInfo(@PathVariable("userId") String userId) {
        return userService.getUserInfo(userId);
    }

    //更新用户
    //
    //允许修改内容
    // 1.头像
    // 2.用户名 唯一
    // 3.签名
    // 4.密码 单独修改
    // 5.邮箱 唯一 单独处理
    @PutMapping("/user_info/{userId}")
    public ResponseResult updateUserInfo(@PathVariable("userId") String userId, @RequestBody BlogUser user) {

        return userService.updateUserInfo(userId, user);
    }

    //查看用户
    //需要管理员权限
    @PreAuthorize("@permission.isAdmin()")
    @GetMapping("/list")
    public ResponseResult getUserList(@RequestParam("page") int page, @RequestParam("size") int size) {
        return userService.getUserList(page, size);
    }

    //删除用户
    //需要管理员权限
    @PreAuthorize("@permission.isAdmin()")
    @DeleteMapping("/{userId}")
    public ResponseResult deleteUser(@PathVariable("userId") String userId) {
        return userService.deleteUser(userId);
    }

    /**
     * 检查该邮箱是否已经注册
     *
     * @param email 邮箱地址
     * @return success 已经注册 fail 未注册
     */
    @ApiResponses({
            @ApiResponse(code = 200, message = "邮箱已经注册"),
            @ApiResponse(code = 400, message = "邮箱未注册")
    })
    @GetMapping("/email")
    public ResponseResult checkEmail(@RequestParam("email") String email) {
        return userService.checkEmail(email);
    }

    /**
     * 检查该用户名是否已经注册
     *
     * @param userName 用户名
     * @return success 已经注册 fail 未注册
     */
    @ApiResponses({
            @ApiResponse(code = 200, message = "用户名已经注册"),
            @ApiResponse(code = 400, message = "用户名未注册")
    })
    @GetMapping("/user_name")
    public ResponseResult checkUserName(@RequestParam("userName") String userName) {
        return userService.checkUserName(userName);
    }


    /**
     * 1.必须已经登录了
     * 2.新的邮箱没有注册过
     * <p>
     * 用户的步骤：
     * 1.已经登录
     * 2。输入新邮箱
     * 3.发送验证码
     * 4.输入验证码
     * 5.提交验证
     * <p>
     * 需要提交的数据：
     * 1.新邮箱
     * 2.验证码
     *
     * @param email
     * @param verifyCode
     * @return
     */
    @PutMapping("/email")
    public ResponseResult updateEmail(@RequestParam("email") String email,
                                      @RequestParam("verify_code") String verifyCode) {
        return userService.updateEmail(email, verifyCode);
    }


    /**
     * 退出登录
     * <p>
     * 1. 删除redis中的token
     * 2。删除mysql中的refresh_token
     * 3. 删除浏览器cookie中的token_key
     *
     * @return
     */
    @GetMapping("/logout")
    public ResponseResult logout() {
        return userService.doLogout();
    }
}

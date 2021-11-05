package net.cz.blog.services.Impl;

import com.sun.corba.se.spi.ior.IdentifiableFactory;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.extern.slf4j.Slf4j;
import net.cz.blog.Dao.SettingsDao;
import net.cz.blog.Dao.UserDao;
import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.BlogUser;
import net.cz.blog.pojo.Setting;
import net.cz.blog.services.IUserService;
import net.cz.blog.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.Random;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements IUserService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private SnowflakeIdWorker idWorker;
    @Autowired
    private UserDao userDao;
    @Autowired
    private SettingsDao settingsDao;

    @Override
    public ResponseResult initManagerAccount(BlogUser user, HttpServletRequest request) {
        //检查是否已经初始化
        Setting managerAccountState = settingsDao.findValByKey(Constants.Settings.MANAGE_ACCOUNT_INIT_STATE);
        if (managerAccountState != null) {
            return ResponseResult.FAILED("管理员账号已经初始化");
        }

        //检查数据
        if (TextUtils.isEmpty(user.getUser_name())) {
            return ResponseResult.FAILED("用户名不能为空");
        }
        if (TextUtils.isEmpty(user.getPassword())) {
            return ResponseResult.FAILED("密码不能为空");
        }
        if (TextUtils.isEmpty(user.getEmail())) {
            return ResponseResult.FAILED("Email不能为空");
        }

        //补充数据
        user.setId(String.valueOf(idWorker.nextId()));
        user.setRoles(Constants.User.ROLE_ADMIN);
        user.setState(Constants.User.DEFAULT_STATE);
        user.setAvatar(Constants.User.DEFAULT_AVATAR);
        String remoteAddress = request.getRemoteAddr();
        String localAddress = request.getLocalAddr();
        user.setLogin_ip(localAddress);
        user.setReg_ip(remoteAddress);
        log.info("remoteAddress-->" + remoteAddress);
        log.info("localAddress-->" + localAddress);
        user.setUpdate_time(new Date());
        user.setCreate_time(new Date());

        //密码加密
        String password = user.getPassword();
        String encode = bCryptPasswordEncoder.encode(password);
        user.setPassword(encode);
        //保持到数据库
        userDao.save(user);

        //更新标记
        Setting setting = new Setting();
        setting.setId(idWorker.nextId() + "");
        setting.setKey(Constants.Settings.MANAGE_ACCOUNT_INIT_STATE);
        setting.setValue("1");
        setting.setCreateTime(new Date());
        setting.setUpdateTime(new Date());
        settingsDao.save(setting);

        return ResponseResult.SUCCESS("管理员账号初始化成功");
    }

    public static final int[] captcha_font_types = {Captcha.FONT_1, Captcha.FONT_2, Captcha.FONT_3, Captcha.FONT_4,
            Captcha.FONT_5, Captcha.FONT_6, Captcha.FONT_7, Captcha.FONT_8, Captcha.FONT_9, Captcha.FONT_10};

    @Autowired
    public Random random;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void createCaptcha(HttpServletResponse response, String captcha_key) throws Exception {
        if (TextUtils.isEmpty(captcha_key) || captcha_key.length() < 13) {
            return;
        }
        long key = 0l;
        try {
            key = Long.parseLong(captcha_key);
        } catch (Exception e) {
            return;
        }
        //key可以用了

        // 设置请求头为输出图片类型
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        int captcha_type = random.nextInt(3);
        Captcha targetCaptcha = null;
        if (captcha_type == 0) {
            // 三个参数分别为宽、高、位数
            targetCaptcha = new SpecCaptcha(200, 60, 5);
        } else if (captcha_type == 1) {
            //gif类型
            targetCaptcha = new GifCaptcha(200, 60);
        } else {
            // 算术类型
            targetCaptcha = new ArithmeticCaptcha(200, 60);
            targetCaptcha.setLen(3);  // 几位数运算，默认是两位
            targetCaptcha.text();  // 获取运算的结果：5
        }

        // 设置字体
        targetCaptcha.setFont(captcha_font_types[random.nextInt(captcha_font_types.length)]);  // 有默认字体，可以不用设置
        // 设置类型
        targetCaptcha.setCharType(Captcha.TYPE_DEFAULT);

        String content = targetCaptcha.text().toLowerCase();
        //保存到redis time代表数据有效时间
        redisUtil.set(Constants.User.KEY_CAPTCHA_CONTENT + key, content, 60 * 10);
        // 输出图片流
        targetCaptcha.out(response.getOutputStream());
    }

    @Autowired
    private TaskService taskService;

    //给邮箱发送验证码
    @Override
    public ResponseResult sendEmail(String emailAddress, HttpServletRequest request) {
        //1.防止暴力发送 1个小时内每个ip发送10次 最少间隔30s发第二次
        String remoteAddr = request.getRemoteAddr();
        if (remoteAddr != null) {
            //处理一下数据 防止在Redis保存的时候套娃
            remoteAddr = remoteAddr.replace(":", "_");
        }
        Integer ipSendTimes = (Integer) redisUtil.get(Constants.User.KEY_EMAIL_SEND_TIMES + remoteAddr);
        if (ipSendTimes != null && ipSendTimes > 10) {
            return ResponseResult.FAILED("您发送邮件过于频繁，请稍后再试");
        }
        //拿到ip是否发送的状态
        Object isSend = redisUtil.get(Constants.User.KEY_EMAIL_SEND_STATE + remoteAddr);
        if (isSend != null) {
            return ResponseResult.FAILED("您发送邮件过于频繁，请稍后再试");
        }
        //2.检查邮箱的格式是否正确
        if (!TextUtils.isEmail(emailAddress)) {
            return ResponseResult.FAILED("邮箱格式有误，请重新输入");
        }
        //3.发送邮件
        int code = random.nextInt(899999) + 100000;
        try {
            taskService.sendEmailVerifyCode(emailAddress, String.valueOf(code));
        } catch (Exception e) {
            return ResponseResult.FAILED("发送邮件失败，请重试~");
        }
        //4.将发送信息保存到Redis数据库中
        //保存发送次数 1个小时有效
        if (ipSendTimes == null) {
            ipSendTimes = 0;
        }
        ipSendTimes++;
        redisUtil.set(Constants.User.KEY_EMAIL_SEND_TIMES + remoteAddr, ipSendTimes, 60 * 10);
        //设置30s无法再次发送
        redisUtil.set(Constants.User.KEY_EMAIL_SEND_STATE + remoteAddr, "true", 30);
        //保存code 10分钟有效
        redisUtil.set(Constants.User.KEY_EMAIL_CODE_CONTENT, String.valueOf(code), 60 * 10);
        return ResponseResult.SUCCESS("验证码发送成功");
    }
}

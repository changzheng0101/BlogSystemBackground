package net.cz.blog.services.Impl;

import com.google.gson.Gson;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import net.cz.blog.Dao.RefreshTokenDao;
import net.cz.blog.Dao.SettingsDao;
import net.cz.blog.Dao.UserDao;
import net.cz.blog.Dao.UserNoPasswordDao;
import net.cz.blog.Response.ResponseResult;
import net.cz.blog.Response.ResponseState;
import net.cz.blog.pojo.BlogUser;
import net.cz.blog.pojo.BlogUserNoPassword;
import net.cz.blog.pojo.RefreshToken;
import net.cz.blog.pojo.Setting;
import net.cz.blog.services.IUserService;
import net.cz.blog.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
@Transactional
public class UserServiceImpl extends BaseService implements IUserService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private SnowflakeIdWorker idWorker;
    @Autowired
    private UserDao userDao;
    @Autowired
    private SettingsDao settingsDao;
    @Autowired
    private RefreshTokenDao refreshTokenDao;
    @Autowired
    private Gson gson;

    @Override
    public ResponseResult initManagerAccount(BlogUser user, HttpServletRequest request) {
        //检查是否已经初始化
        Setting managerAccountState = settingsDao.findOneByKey(Constants.Settings.MANAGE_ACCOUNT_INIT_STATE);
        if (managerAccountState != null) {
            return ResponseResult.FAILED("管理员账号已经初始化");
        }

        //检查数据
        if (TextUtils.isEmpty(user.getUserName())) {
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
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

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
    private Random random;
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
        log.info("sendVerifyCode==>" + content);
        // 输出图片流
        targetCaptcha.out(response.getOutputStream());
    }

    @Autowired
    private TaskService taskService;

    //给邮箱发送验证码
    //业务场景 注册 找回密码 更换邮箱（会输入新的邮箱）
    //注册(register)：如果已经注册，提示已经注册
    //找回密码(forget)：邮箱未注册的话，提示未注册
    //更换邮箱(update)：查看新的邮箱是否已经注册
    //用一个type来区别这几种情况
    @Override
    public ResponseResult sendEmail(String type, String emailAddress, HttpServletRequest request) {
        if (emailAddress == null) {
            return ResponseResult.FAILED("邮箱地址不可以为空");
        }
        if ("register".equals(type) || "update".equals(type)) {
            BlogUser userByEmail = userDao.findOneByEmail(emailAddress);
            if (userByEmail != null) {
                return ResponseResult.FAILED("该邮箱已经被注册");
            }
        } else if ("forget".equals(type)) {
            BlogUser userByEmail = userDao.findOneByEmail(emailAddress);
            if (userByEmail == null) {
                return ResponseResult.FAILED("该邮箱未注册");
            }
        }

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
        Object isSend = redisUtil.get(Constants.User.KEY_EMAIL_SEND_STATE + emailAddress);
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
        redisUtil.set(Constants.User.KEY_EMAIL_SEND_STATE + emailAddress, "true", 30);
        //保存code 10分钟有效
        redisUtil.set(Constants.User.KEY_EMAIL_CODE_CONTENT + emailAddress, String.valueOf(code), 60 * 10);
        return ResponseResult.SUCCESS("验证码发送成功");
    }


    @Override
    public ResponseResult register(BlogUser user, String emailVerifyCode, String captcha, String captchaKey,
                                   HttpServletRequest request) {
        //第一步：检查当前用户名是否已经注册
        String userName = user.getUserName();
        if (userName == null) {
            return ResponseResult.FAILED("用户名不可以为空");
        }
        BlogUser userByUserName = userDao.findOneByUserName(userName);
        if (userByUserName != null) {
            return ResponseResult.FAILED("该用户已经被注册");
        }
        //第二步：检查邮箱格式是否正确
        String email = user.getEmail();
        if (email.isEmpty()) {
            return ResponseResult.FAILED("邮箱不可以为空");
        }
        if (!TextUtils.isEmail(email)) {
            return ResponseResult.FAILED("邮箱格式不正确");
        }
        //第三步：检查邮箱是否已经被注册
        BlogUser userByEmail = userDao.findOneByEmail(email);
        if (userByEmail != null) {
            return ResponseResult.FAILED("该邮箱已经被注册");
        }
        //第四步：检查邮箱验证码是否正确
        String emailVerifyCodeCorrect = (String) redisUtil.get(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        if (TextUtils.isEmpty(emailVerifyCodeCorrect)) {
            return ResponseResult.FAILED("验证码已过期");
        }
        if (!emailVerifyCode.equals(emailVerifyCodeCorrect)) {
            return ResponseResult.FAILED("邮箱验证码错误");
        }
        //第五步：检查图灵验证码是否正确
        String captchaCorrect = (String) redisUtil.get(Constants.User.KEY_CAPTCHA_CONTENT + captchaKey);
        if (captchaCorrect == null) {
            return ResponseResult.FAILED("邮箱验证码已经过期");
        }
        if (!captchaCorrect.equals(captcha)) {
            return ResponseResult.FAILED("验证码错误");
        }
        //达到可以注册的条件
        //第六步：对密码进行加密
        if (user.getPassword() == null) {
            return ResponseResult.FAILED("密码不能为空");
        }
        if (user.getPassword().length() < 6) {
            return ResponseResult.FAILED("密码长度过短");
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        //第七步：补全数据
        user.setId(String.valueOf(idWorker.nextId()));
        user.setRoles(Constants.User.ROLE_NORMAL);
        user.setState(Constants.User.DEFAULT_STATE);
        user.setAvatar(Constants.User.DEFAULT_AVATAR);
        String remoteAddress = request.getRemoteAddr();
        String localAddress = request.getLocalAddr();
        user.setLogin_ip(localAddress);
        user.setReg_ip(remoteAddress);
        user.setUpdateTime(new Date());
        user.setCreateTime(new Date());
        //第八步：保存到数据库
        userDao.save(user);
        //第九步： 删除应该删除的数据
        redisUtil.del(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        redisUtil.del(Constants.User.KEY_CAPTCHA_CONTENT + captchaKey);
        //第十步：返回结果
        return ResponseResult.GET(ResponseState.JOIN_IN_SUCCESS);
    }

    @Override
    public ResponseResult doLogin(String captcha, String captchaKey, BlogUser user,
                                  HttpServletRequest request, HttpServletResponse response) {
        String captchaCorrect = (String) redisUtil.get(Constants.User.KEY_CAPTCHA_CONTENT + captchaKey);
        if (!captchaCorrect.equals(captcha)) {
            return ResponseResult.FAILED("人类验证码不正确");
        }
        //验证成功 删除redis中的数据
        redisUtil.del(Constants.User.KEY_CAPTCHA_CONTENT + captchaKey);
        //用户名可能为邮箱也可能为用户名
        String userAccount = user.getUserName();
        if (TextUtils.isEmpty(userAccount)) {
            return ResponseResult.FAILED("账号不可以为空");
        }
        String password = user.getPassword();
        if (TextUtils.isEmpty(password)) {
            return ResponseResult.FAILED("密码不可以为空");
        }

        BlogUser userFromDb = userDao.findOneByUserName(userAccount);
        if (userFromDb == null) {
            userFromDb = userDao.findOneByEmail(userAccount);
        }
        if (userFromDb == null) {
            return ResponseResult.FAILED("用户名或密码错误");
        }

        //用户存在
        //对比密码
        boolean matches = bCryptPasswordEncoder.matches(password, userFromDb.getPassword());
        if (!matches) {
            return ResponseResult.FAILED("用户名或密码错误");
        }
        // 密码正确
        // 判断状态是否正常
        if (!"1".equals(userFromDb.getState())) {
            return ResponseResult.FAILED("该账号已被禁止");
        }
        createToken(response, userFromDb);
        return ResponseResult.SUCCESS("用户认证成功");
    }

    private String createToken(HttpServletResponse response, BlogUser userFromDb) {
        refreshTokenDao.deleteAllByUserId(userFromDb.getId());
        // 生成token
        Map<String, Object> claims = ClaimsUtils.blogUser2Claims(userFromDb);
        String token = JwtUtil.createToken(claims);
        //返回token的md5值 token会保存到redis中
        //前端访问的时候，会携带md5的tokenKey，再从redis中获取即可
        String tokenKey = DigestUtils.md5DigestAsHex(token.getBytes());
        log.info("tokenKey==>" + tokenKey);
        //保存到redis中 有效期为两个小时
        redisUtil.set(Constants.User.KEY_TOKEN + tokenKey, token, Constants.TimeValue.HOUR * 2);
        //把tokenKey写到cookie中
        CookieUtils.setUpCookie(response, Constants.User.COOKIE_TOKEN_KEY, tokenKey);
        //生成refresh token
        String refreshTokenVal = JwtUtil.createRefreshToken(userFromDb.getId(), Constants.TimeValue.YEAR * 1000L);
        //保存到数据库
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(idWorker.nextId() + "");
        refreshToken.setRefreshToken(refreshTokenVal);
        refreshToken.setUserId(userFromDb.getId());
        refreshToken.setTokenKey(tokenKey);
        refreshToken.setCreateTime(new Date());
        refreshToken.setUpdateTime(new Date());
        refreshTokenDao.save(refreshToken);

        return tokenKey;
    }

    @Override
    public BlogUser checkBolgUser() {
        //拿到token_key 注意保存的时候有前缀
        String tokenKey = CookieUtils.getCookie(getRequest(), Constants.User.COOKIE_TOKEN_KEY);
        BlogUser blogUser = parseByTokenKey(tokenKey);
        if (blogUser == null) {
            //说明无法识别token
            //1.去mysql中查找有没有refreshToken
            RefreshToken refreshToken = refreshTokenDao.findOneByTokenKey(tokenKey);
            //2.如果没有 则当前用户未登录 提示登录
            if (refreshToken == null) {
                return null;
            }
            //3.如果存在 解析refreshToken 并保存新的数据
            try {
                JwtUtil.parseJWT(refreshToken.getRefreshToken());
                //5.如果refreshToken有效 创建新的refreshToken和token
                String userId = refreshToken.getUserId();
                BlogUser userFromDb = userDao.findOneById(userId);
                //这里不要直接返回user  这个user带密码 会泄露密码
                // 该方法不可取 会直接将数据库中的密码置空
//                userFromDb.setPassword("");
                String newTokenKey = createToken(getResponse(), userFromDb);
                log.info("create new token and refresh token");
                //返回token
                return parseByTokenKey(newTokenKey);
            } catch (Exception e) {
                //4.解析refreshToken失败 表示没有登录 提示用户登录
                return null;
            }
        }
        return blogUser;
    }

    @Override
    public ResponseResult getUserInfo(String userId) {
        BlogUser userFromDb = userDao.findOneById(userId);
        //为空 表示未找到
        if (userFromDb == null) {
            return ResponseResult.FAILED("用户不存在");
        }
        //对敏感信息进行处理
        String userJson = gson.toJson(userFromDb);
        BlogUser newUser = gson.fromJson(userJson, BlogUser.class);
        newUser.setPassword("");
        newUser.setEmail("");
        newUser.setReg_ip("");
        newUser.setRoles("");
        newUser.setRoles("");
        newUser.setLogin_ip("");
        newUser.setCreateTime(null);
        newUser.setUpdateTime(null);
        return ResponseResult.SUCCESS("用户查询成功").setData(newUser);
    }

    @Override
    public ResponseResult checkEmail(String email) {
        BlogUser userFromEmail = userDao.findOneByEmail(email);
        return userFromEmail == null ? ResponseResult.FAILED("该邮箱已经被注册") : ResponseResult.SUCCESS("该邮箱未被注册");
    }

    @Override
    public ResponseResult checkUserName(String userName) {
        BlogUser userFromUserName = userDao.findOneByUserName(userName);
        return userFromUserName == null ? ResponseResult.FAILED("该用户名已经被注册") :
                ResponseResult.SUCCESS("该用户名未被注册");
    }


    @Override
    public ResponseResult updateUserInfo(String userId, BlogUser user) {
        //该对象不是数据库中的 只是携带了部分的信息
        BlogUser blogUserByTokenKey = checkBolgUser();
        if (blogUserByTokenKey == null) {
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }
        //判断用户id是否一致 一致才可以进行修改
        if (!blogUserByTokenKey.getId().equals(userId)) {
            return ResponseResult.PERMISSION_FORBID();
        }
        //权限ok 进行修改
        //首先获取对象
        BlogUser userFromDb = userDao.findOneById(userId);
        //修改名称
        if (user.getUserName() != null) {
            if (userDao.findOneByUserName(user.getUserName()) == null) {
                userFromDb.setUserName(user.getUserName());
            }
        }
        //修改头像 不可为空
        if (user.getAvatar() != null) {
            userFromDb.setAvatar(user.getAvatar());
        }
        //签名 可为空 如果不传入这个参数 会把签名直接置空
        userFromDb.setSign(user.getSign());
        userFromDb.setUpdateTime(new Date());
        //保存
        userDao.save(userFromDb);
        //拿到request和response
        HttpServletRequest request = getRequest();
        //干掉redis中的token  否则redis中的信息还是上次的
        String tokenKey = CookieUtils.getCookie(request, Constants.User.COOKIE_TOKEN_KEY);
        redisUtil.del(tokenKey);
        return ResponseResult.SUCCESS("用户信息更新成功");
    }


    private HttpServletResponse getResponse() {
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes.getResponse();
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes.getRequest();
    }

    @Override
    public ResponseResult deleteUser(String userId) {
        //删除用户
        int result = userDao.deleteBlogUserByState(userId);
        if (result > 0) {
            return ResponseResult.SUCCESS("删除成功");
        }
        return ResponseResult.FAILED("用户不存在");
    }

    @Autowired
    private UserNoPasswordDao userNoPasswordDao;

    @Override
    public ResponseResult getUserList(int page, int size) {
        //可以查询了
        page = checkPage(page);
        size = checkSize(size);
        //根据注册日期来排序
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        List<BlogUserNoPassword> userList = userNoPasswordDao.findAll();
        return ResponseResult.SUCCESS("用户查询成功").setData(userList);
    }

    @Override
    public ResponseResult updatePassword(String verifyCode, BlogUser user) {
        //检查邮箱是否填写
        String email = user.getEmail();
        if (TextUtils.isEmail(email)) {
            return ResponseResult.FAILED("邮箱不可以为空");
        }
        //根据邮箱去redis中进行验证
        String verifyCodeCorrect = (String) redisUtil.get(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        if (verifyCodeCorrect == null || !verifyCodeCorrect.equals(verifyCode)) {
            return ResponseResult.FAILED("验证码不正确");
        }
        //删除 节省资源
        redisUtil.del(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        int result = userDao.updatePasswordByEmail(bCryptPasswordEncoder.encode(user.getPassword()), email);
        return result > 0 ? ResponseResult.SUCCESS("密码修改成功") : ResponseResult.FAILED("密码修改 失败");
    }

    @Override
    public ResponseResult updateEmail(String email, String verifyCode) {
        // 确保登录
        BlogUser blogUser = checkBolgUser();
        if (blogUser == null) {
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }
        // 新的邮箱是用户的
        String verifyCodeCorrect = (String) redisUtil.get(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        if (verifyCodeCorrect == null || !verifyCodeCorrect.equals(verifyCode)) {
            return ResponseResult.FAILED("验证码不正确");
        }
        //删除验证码
        redisUtil.del(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        //修改邮箱
        int result = userDao.updateEmailById(email, blogUser.getId());
        return result > 0 ? ResponseResult.SUCCESS("邮箱修改成功") : ResponseResult.FAILED("邮箱修改失败");
    }

    @Override
    public ResponseResult doLogout() {
        //拿到token_key
        String tokenKey = CookieUtils.getCookie(getRequest(), Constants.User.COOKIE_TOKEN_KEY);
        if (tokenKey == null) {
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }
        //删除redis中的数据
        redisUtil.del(Constants.User.KEY_TOKEN + tokenKey);
        //删除mysql中的refreshToken
        refreshTokenDao.deleteAllByTokenKey(tokenKey);
        //删除cookie
        CookieUtils.delCookie(getResponse(), Constants.User.COOKIE_TOKEN_KEY);
        return ResponseResult.SUCCESS("用户退出登录成功");
    }


    private BlogUser parseByTokenKey(String tokenKey) {
        String token = (String) redisUtil.get(Constants.User.KEY_TOKEN + tokenKey);
        if (token != null) {
            try {
                Claims claims = JwtUtil.parseJWT(token);
                return ClaimsUtils.claims2BlogUser(claims);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }


}

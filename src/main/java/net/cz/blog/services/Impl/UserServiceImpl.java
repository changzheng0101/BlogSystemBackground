package net.cz.blog.services.Impl;

import lombok.extern.slf4j.Slf4j;
import net.cz.blog.Dao.SettingsDao;
import net.cz.blog.Dao.UserDao;
import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.BlogUser;
import net.cz.blog.pojo.Setting;
import net.cz.blog.services.IUserService;
import net.cz.blog.utils.Constants;
import net.cz.blog.utils.SnowflakeIdWorker;
import net.cz.blog.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Date;

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
}

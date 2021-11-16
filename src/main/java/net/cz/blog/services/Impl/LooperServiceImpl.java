package net.cz.blog.services.Impl;

import net.cz.blog.Dao.LooperDao;
import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.Looper;
import net.cz.blog.services.ILooperService;
import net.cz.blog.utils.SnowflakeIdWorker;
import net.cz.blog.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


@Service
@Transactional
public class LooperServiceImpl extends BaseService implements ILooperService {

    @Autowired
    private SnowflakeIdWorker idWorker;

    @Autowired
    private LooperDao looperDao;

    @Override
    public ResponseResult addLooper(Looper looper) {
        //检查数据
        if (TextUtils.isEmpty(looper.getTitle())) {
            return ResponseResult.FAILED("轮播图标题不可以为空");
        }
        if (TextUtils.isEmpty(looper.getTargetUrl())) {
            return ResponseResult.FAILED("目标url不可以为空");
        }
        if (TextUtils.isEmpty(looper.getImageUrl())) {
            return ResponseResult.FAILED("图片地址不可以为空");
        }
        //补全数据
        looper.setId(idWorker.nextId() + "");
        looper.setCreateTime(new Date());
        looper.setUpdateTime(new Date());
        //保存数据
        looperDao.save(looper);
        return ResponseResult.SUCCESS("轮播图添加成功");
    }
}

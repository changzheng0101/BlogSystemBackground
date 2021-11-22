package net.cz.blog.services.Impl;

import net.cz.blog.Dao.LooperDao;
import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.Looper;
import net.cz.blog.services.ILooperService;
import net.cz.blog.utils.SnowflakeIdWorker;
import net.cz.blog.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        //判断数据
        if (TextUtils.isEmpty(looper.getTitle())) {
            return ResponseResult.FAILED("标题不可以为空");
        }
        if (TextUtils.isEmpty(looper.getImageUrl())) {
            return ResponseResult.FAILED("图片不可以为空");
        }
        if (TextUtils.isEmpty(looper.getTargetUrl())) {
            return ResponseResult.FAILED("跳转连接不可以为空");
        }
        //补全数据
        looper.setId(idWorker.nextId() + "");
        looper.setCreateTime(new Date());
        looper.setUpdateTime(new Date());
        //保存
        looperDao.save(looper);
        return ResponseResult.SUCCESS("轮播图添加成功");
    }

    @Override
    public ResponseResult getLooperList(int page, int size) {
        page = checkPage(page);
        size = checkSize(size);
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Looper> all = looperDao.findAll(pageable);
        return ResponseResult.SUCCESS("获取数据列表成功").setData(all);
    }

    @Override
    public ResponseResult getLooper(String looperId) {
        Looper looper = looperDao.findOneById(looperId);
        if (looper == null) {
            return ResponseResult.FAILED("轮播图不存在");
        }
        return ResponseResult.SUCCESS("轮播图查询成功").setData(looper);
    }

    @Override
    public ResponseResult deleteLooper(String looperId) {
        looperDao.deleteById(looperId);
        return ResponseResult.SUCCESS("删除成功");
    }

    @Override
    public ResponseResult updateLooper(String looperId, Looper looper) {
        Looper looperFromDb = looperDao.findOneById(looperId);
        if (looperFromDb == null) {
            return ResponseResult.FAILED("轮播图不存在");
        }
        //更新数据
        String title = looper.getTitle();
        if (!TextUtils.isEmpty(title)) {
            looperFromDb.setTitle(title);
        }
        String imageUrl = looper.getImageUrl();
        if (!TextUtils.isEmpty(imageUrl)) {
            looperFromDb.setImageUrl(imageUrl);
        }
        String targetUrl = looper.getTargetUrl();
        if (!TextUtils.isEmpty(targetUrl)) {
            looperFromDb.setTargetUrl(targetUrl);
        }
        long order = looper.getOrder();
        if (order != looperFromDb.getOrder()) {
            looperFromDb.setOrder(order);
        }
        looperFromDb.setUpdateTime(new Date());
        //保存数据
        looperDao.save(looperFromDb);
        return ResponseResult.SUCCESS("轮播图数据更新成功");
    }
}


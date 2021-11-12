package net.cz.blog.services.Impl;

import net.cz.blog.Dao.CategoryDao;
import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.Category;
import net.cz.blog.services.ICategoryService;
import net.cz.blog.utils.SnowflakeIdWorker;
import net.cz.blog.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private SnowflakeIdWorker idWorker;
    @Autowired
    private CategoryDao categoryDao;


    @Override
    public ResponseResult addCategory(Category category) {
        //检查数据
        if (TextUtils.isEmpty(category.getName())) {
            return ResponseResult.FAILED("分类名称不能为空");
        }
        if (TextUtils.isEmpty(category.getPinyin())) {
            return ResponseResult.FAILED("分类拼音不能为空");
        }
        if (TextUtils.isEmpty(category.getDescription())) {
            return ResponseResult.FAILED("分类描述不能为空");
        }
        //补齐数据
        category.setId(idWorker.nextId() + "");
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());
        category.setStatus("1");
        //保存数据
        categoryDao.save(category);
        return ResponseResult.SUCCESS("分类添加成功");
    }

    @Override
    public ResponseResult getCategory(String categoryId) {
        Category category = categoryDao.findOneById(categoryId);
        if (category == null) {
            return ResponseResult.FAILED("分类未找到");
        }
        return ResponseResult.SUCCESS("分类查询成功").setData(category);
    }
}

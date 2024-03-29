package net.cz.blog.services.Impl;

import lombok.extern.slf4j.Slf4j;
import net.cz.blog.Dao.CategoryDao;
import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.BlogUser;
import net.cz.blog.pojo.Category;
import net.cz.blog.services.ICategoryService;
import net.cz.blog.services.IUserService;
import net.cz.blog.utils.Constants;
import net.cz.blog.utils.SnowflakeIdWorker;
import net.cz.blog.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
@Slf4j
public class CategoryServiceImpl extends BaseService implements ICategoryService {

    @Autowired
    private SnowflakeIdWorker idWorker;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private IUserService userService;


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

    /**
     * 管理员：可以获取所有分类 包括删除的
     * 其余： 只能获取状态为1的
     *
     * @return
     */
    @Override
    public ResponseResult getCategoryList() {
        BlogUser blogUser = userService.checkBolgUser();
        List<Category> categories;
        if (blogUser == null || !Constants.User.ROLE_ADMIN.equals(blogUser.getRoles())) {
            //处理其余情况
            categories = categoryDao.listCategoryByStatus("1");
        } else {
            //创建条件
            Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
            //查询
            categories = categoryDao.findAll(sort);
        }
        return ResponseResult.SUCCESS("查询列表成功").setData(categories);
    }

    @Override
    public ResponseResult updateCategory(String categoryId, Category category) {
        Category categoryFromDb = categoryDao.findOneById(categoryId);
        if (categoryFromDb == null) {
            return ResponseResult.FAILED("分类不存在");
        }
        //判断内容
        if (!TextUtils.isEmpty(category.getName())) {
            categoryFromDb.setName(category.getName());
        }
        if (!TextUtils.isEmpty(category.getPinyin())) {
            categoryFromDb.setPinyin(category.getPinyin());
        }
        if (!TextUtils.isEmpty(category.getDescription())) {
            categoryFromDb.setDescription(category.getDescription());
        }
        if (!TextUtils.isEmpty(category.getOrder() + "")) {
            categoryFromDb.setOrder(category.getOrder());
        }
        categoryFromDb.setStatus(category.getStatus());
        categoryFromDb.setUpdateTime(new Date());
        //保存数据
        categoryDao.save(categoryFromDb);
        return ResponseResult.SUCCESS("分类更新成功").setData(categoryFromDb);
    }

    @Override
    public ResponseResult deleteCategory(String categoryId) {
        int result = categoryDao.deleteCategoryByUpdateStatus(categoryId);
        if (result == 0) {
            return ResponseResult.FAILED("该分类不存在");
        }
        return ResponseResult.SUCCESS("删除分类成功");
    }
}

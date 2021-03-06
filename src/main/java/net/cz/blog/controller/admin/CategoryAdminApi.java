package net.cz.blog.controller.admin;

import net.cz.blog.Response.ResponseResult;
import net.cz.blog.interceptor.checkTooFrequentCommit;
import net.cz.blog.pojo.Category;
import net.cz.blog.services.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

//分类相关的api
@RestController
@RequestMapping("/admin/category")
public class CategoryAdminApi {

    @Autowired
    private ICategoryService categoryService;

    //增加分类
    @PreAuthorize("@permission.isAdmin()")
    @PostMapping
    @checkTooFrequentCommit
    public ResponseResult addCategory(@RequestBody Category category) {
        return categoryService.addCategory(category);
    }

    //删除分类
    @PreAuthorize("@permission.isAdmin()")
    @DeleteMapping("/{categoryId}")
    public ResponseResult deleteCategory(@PathVariable("categoryId") String categoryId) {
        return categoryService.deleteCategory(categoryId);
    }

    //修改某个类
    @PreAuthorize("@permission.isAdmin()")
    @PutMapping("/{categoryId}")
    @checkTooFrequentCommit
    public ResponseResult updateCategory(@PathVariable("categoryId") String categoryId, @RequestBody Category category) {
        return categoryService.updateCategory(categoryId,category);
    }

    //获取某个分类
    @PreAuthorize("@permission.isAdmin()")
    @GetMapping("/{categoryId}")
    public ResponseResult getCategory(@PathVariable("categoryId") String categoryId) {
        return categoryService.getCategory(categoryId);
    }

    //获取整个分类列表 获取部分
    @PreAuthorize("@permission.isAdmin()")
    @GetMapping("/category_list")
    public ResponseResult getCategoryList() {
        return categoryService.getCategoryList();
    }
}

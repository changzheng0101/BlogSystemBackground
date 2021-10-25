package net.cz.blog.controller.admin;

import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.Category;
import org.springframework.web.bind.annotation.*;

//分类相关的api
@RestController
@RequestMapping("/admin/category")
public class CategoryAdminApi {
    //增加分类
    @PostMapping
    public ResponseResult addCategory(@RequestBody Category category) {
        return null;
    }

    //删除分类
    @DeleteMapping("/{categoryId}")
    public ResponseResult deleteCategory(@PathVariable("categoryId") String categoryId) {
        return null;
    }

    //修改某个类
    @PutMapping("/{categoryId}")
    public ResponseResult updateCategory(@PathVariable("categoryId") String categoryId, @RequestBody Category category) {
        return null;
    }

    //获取某个分类
    @GetMapping("/{categoryId}")
    public ResponseResult getCategory(@PathVariable("categoryId") String categoryId) {
        return null;
    }

    //获取整个分类列表 获取部分
    @GetMapping("/category_list")
    public ResponseResult getCategoryList(@RequestParam("page") int page, @RequestParam("size") int size) {
        return null;
    }
}

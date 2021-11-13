package net.cz.blog.services;

import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.Category;

public interface ICategoryService {
    ResponseResult addCategory(Category category);

    ResponseResult getCategory(String categoryId);

    ResponseResult getCategoryList(int page, int size);

    ResponseResult updateCategory(String categoryId, Category category);
}

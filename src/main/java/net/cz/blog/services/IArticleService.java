package net.cz.blog.services;

import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.Article;

public interface IArticleService {
    ResponseResult postArticle(Article article);

    ResponseResult getArticleList(int page, int size, String state, String categoryId, String keyword);

    ResponseResult getArticle(String articleId);

}

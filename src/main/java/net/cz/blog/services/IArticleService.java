package net.cz.blog.services;

import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.Article;

public interface IArticleService {
    ResponseResult postArticle(Article article);
}

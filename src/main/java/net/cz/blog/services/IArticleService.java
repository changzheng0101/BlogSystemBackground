package net.cz.blog.services;

import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.Article;

public interface IArticleService {
    ResponseResult postArticle(Article article);

    ResponseResult getArticleList(int page, int size, String state, String categoryId, String keyword);

    ResponseResult getArticle(String articleId);

    ResponseResult updateArticle(String articleId, Article article);

    ResponseResult deleteArticleById(String articleId);

    ResponseResult topArticle(String articleId);

    ResponseResult deleteArticleByChangeState(String articleId);

    ResponseResult getTopArticleList();


    ResponseResult getRecommendArticles(String articleId, int size);

    ResponseResult getArticleListByLabel(String label, int page, int size);

    ResponseResult getLabels(int size);
}

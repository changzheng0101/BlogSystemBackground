package net.cz.blog.controller.portal;

import net.cz.blog.Response.ResponseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portal/article")
public class ArticlePortalApi {
    @GetMapping("/list/{page}/{size}")
    public ResponseResult listArticle(@PathVariable("page") int page, @PathVariable("size") int size) {
        return null;
    }

    @GetMapping("list/{categoryId}/{page}/{size}")
    public ResponseResult getArticleByCategory(@PathVariable("categoryId") String categoryId,
                                               @PathVariable("page") int page,
                                               @PathVariable("size") int size) {
        return null;
    }

    @GetMapping("/{articleId}")
    public ResponseResult getArticleDetail(@PathVariable("articleId") String articleId) {
        return null;
    }

    //    根据文章的id 获取该文章的推荐列表
    @GetMapping("/recommend/{articleId}")
    public ResponseResult getRecommendArticles(@PathVariable("articleId") String articleId) {
        return null;
    }
}

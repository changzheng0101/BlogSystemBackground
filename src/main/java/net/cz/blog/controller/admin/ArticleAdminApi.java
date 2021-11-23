package net.cz.blog.controller.admin;

import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.Article;
import net.cz.blog.services.IArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/article")
public class ArticleAdminApi {

    @Autowired
    private IArticleService articleService;

    //增删改查 还有列出所有数据的方法
    @PostMapping
    public ResponseResult postArticle(@RequestBody Article article) {
        return articleService.postArticle(article);
    }

    @DeleteMapping("/{articleId}")
    public ResponseResult deleteArticle(@PathVariable("articleId") String articleId) {
        return null;
    }

    @PutMapping("/{articleId}")
    public ResponseResult updateArticle(@PathVariable("articleId") String articleId) {
        return null;
    }

    @GetMapping("/{articleId}")
    public ResponseResult getArticle(@PathVariable("articleId") String articleId) {
        return articleService.getArticle(articleId);
    }

    @GetMapping("/list/{page}/{size}")
    public ResponseResult getArticleList(@PathVariable("page") int page, @PathVariable("size") int size,
                                         @RequestParam("state") String state,
                                         @RequestParam("categoryId") String categoryId,
                                         @RequestParam("keyword") String keyword) {
        return articleService.getArticleList(page, size, state, categoryId, keyword);
    }

    //改变文章状态
    @PutMapping("/state/{articleId}/{state}")
    public ResponseResult updateArticleState(@PathVariable("articleId") String articleId,
                                             @PathVariable("state") String state) {
        return null;
    }

    //置顶文章
    @PutMapping("/top/{articleId}")
    public ResponseResult updateArticleState(@PathVariable("articleId") String articleId) {
        return null;
    }
}

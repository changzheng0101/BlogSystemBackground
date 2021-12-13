package net.cz.blog.controller.admin;

import net.cz.blog.Response.ResponseResult;
import net.cz.blog.interceptor.checkTooFrequentCommit;
import net.cz.blog.pojo.Article;
import net.cz.blog.services.IArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/article")
public class ArticleAdminApi {

    @Autowired
    private IArticleService articleService;

    //增删改查 还有列出所有数据的方法
    @checkTooFrequentCommit
    @PostMapping
    public ResponseResult postArticle(@RequestBody Article article) {
        return articleService.postArticle(article);
    }

    /**
     * 删除文章，多用户系统，用户只能改变状态，管理员才可以真正的删除
     * 这里做成真的删除
     *
     * @param articleId
     * @return
     */
    @PreAuthorize("@permission.isAdmin()")
    @DeleteMapping("/{articleId}")
    public ResponseResult deleteArticle(@PathVariable("articleId") String articleId) {
        return articleService.deleteArticleById(articleId);
    }

    @PreAuthorize("@permission.isAdmin()")
    @PutMapping("/{articleId}")
    @checkTooFrequentCommit
    public ResponseResult updateArticle(@PathVariable("articleId") String articleId, @RequestParam Article article) {
        return articleService.updateArticle(articleId, article);
    }

    @GetMapping("/{articleId}")
    public ResponseResult getArticle(@PathVariable("articleId") String articleId) {
        return articleService.getArticle(articleId);
    }

    @PreAuthorize("@permission.isAdmin()")
    @GetMapping("/list/{page}/{size}")
    public ResponseResult getArticleList(@PathVariable("page") int page, @PathVariable("size") int size,
                                         @RequestParam("state") String state,
                                         @RequestParam("categoryId") String categoryId,
                                         @RequestParam("keyword") String keyword) {
        return articleService.getArticleList(page, size, state, categoryId, keyword);
    }

    @GetMapping("list/{label}/{page}/{size}")
    public ResponseResult getArticleListByLabels(@PathVariable("label") String label,
                                                 @PathVariable("page") int page,
                                                 @PathVariable("size") int size) {
        return articleService.getArticleListByLabel(label, page, size);
    }

    //改变文章状态
    @DeleteMapping("/state/{articleId}")
    public ResponseResult deleteArticleByChangeState(@PathVariable("articleId") String articleId) {
        return articleService.deleteArticleByChangeState(articleId);
    }

    //置顶文章
    @PreAuthorize("@permission.isAdmin()")
    @PutMapping("/top/{articleId}")
    public ResponseResult topArticle(@PathVariable("articleId") String articleId) {
        return articleService.topArticle(articleId);
    }

    /**
     * 获取标签云
     * 如果用户点击标签，就会跳转到相应页面
     *
     * @param size
     * @return
     */
    @GetMapping("/list/{size}")
    public ResponseResult getLabels(@PathVariable("size") int size) {
        return articleService.getLabels(size);
    }
}

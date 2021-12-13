package net.cz.blog.controller.portal;

import net.cz.blog.Response.ResponseResult;
import net.cz.blog.services.IArticleService;
import net.cz.blog.services.ICategoryService;
import net.cz.blog.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portal/article")
public class ArticlePortalApi {

    @Autowired
    private IArticleService articleService;
    @Autowired
    private ICategoryService categoryService;

    /**
     * 针对所有用户
     * 状态：必须是已经发布的，置顶的由另一个接口获取
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list/{page}/{size}")
    public ResponseResult listArticle(@PathVariable("page") int page, @PathVariable("size") int size) {
        return articleService.getArticleList(page, size, Constants.Article.ARTICLE_PUBLISH, null, null);
    }

    @GetMapping("list/{categoryId}/{page}/{size}")
    public ResponseResult getArticleByCategory(@PathVariable("categoryId") String categoryId,
                                               @PathVariable("page") int page,
                                               @PathVariable("size") int size) {
        return articleService.getArticleList(page, size, Constants.Article.ARTICLE_PUBLISH, categoryId, null);
    }

    /**
     * 普通用户只可以获取置顶和发布的文章
     * 用户本人可以获取草稿
     * 管理员才可以获取删除的
     *
     * @param articleId
     * @return
     */
    @GetMapping("/{articleId}")
    public ResponseResult getArticleDetail(@PathVariable("articleId") String articleId) {
        return articleService.getArticle(articleId);
    }

    /**
     * 通过标签获取推荐文章
     * 每次随机选择一个标签，去数据库中查找，保证每次不那么雷同
     * 如果查找不到相同的文章
     * 那就从最新的文章中进行获取
     *
     * @param articleId
     * @param size
     * @return
     */
    @GetMapping("/recommend/{articleId}/{size}")
    public ResponseResult getRecommendArticles(@PathVariable("articleId") String articleId,
                                               @PathVariable("size") int size) {
        return articleService.getRecommendArticles(articleId, size);
    }

    @GetMapping("/top")
    public ResponseResult getTopArticleList() {
        return articleService.getTopArticleList();
    }

    @GetMapping("/categories")
    public ResponseResult getCategories() {
        return categoryService.getCategoryList();
    }
}

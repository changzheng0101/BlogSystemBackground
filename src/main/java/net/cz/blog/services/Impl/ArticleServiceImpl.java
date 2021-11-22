package net.cz.blog.services.Impl;

import net.cz.blog.Dao.ArticleDao;
import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.Article;
import net.cz.blog.pojo.BlogUser;
import net.cz.blog.services.IArticleService;
import net.cz.blog.services.IUserService;
import net.cz.blog.utils.Constants;
import net.cz.blog.utils.SnowflakeIdWorker;
import net.cz.blog.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.rmi.StubNotFoundException;
import java.util.Date;

@Service
@Transactional
public class ArticleServiceImpl extends BaseService implements IArticleService {

    @Autowired
    private IUserService userService;
    @Autowired
    private SnowflakeIdWorker idWorker;
    @Autowired
    private ArticleDao articleDao;


    @Override
    public ResponseResult postArticle(Article article) {
        //检查数据 标题、分类、内容、类型、摘要、标签
        BlogUser blogUser = userService.checkBolgUser();
        String title = article.getTitle();
        if (TextUtils.isEmpty(title)) {
            return ResponseResult.FAILED("文章标题不可以为空");
        }
        if (title.length() > Constants.Article.TITLE_MAX_LENGTH) {
            return ResponseResult.FAILED("文章标题过长");
        }
        String getCategoryId = article.getCategoryId();
        if (TextUtils.isEmpty(getCategoryId)) {
            return ResponseResult.FAILED("文章分类id不可以为空");
        }
        String content = article.getContent();
        if (TextUtils.isEmpty(content)) {
            return ResponseResult.FAILED("内容不可以为空");
        }
        String type = article.getType();
        if (TextUtils.isEmpty(type)) {
            return ResponseResult.FAILED("文章类型可以为空");
        }
        if (!"0".equals(type) && !"1".equals(type)) {
            return ResponseResult.FAILED("文章类型有误");
        }
        String summary = article.getSummary();
        if (TextUtils.isEmpty(summary)) {
            return ResponseResult.FAILED("文章摘要不可以为空");
        }
        if (summary.length() > Constants.Article.SUMMARY_MAX_LENGTH) {
            return ResponseResult.FAILED("摘要过长");
        }
        String labels = article.getLabels();
        if (TextUtils.isEmpty(labels)) {
            return ResponseResult.FAILED("文章标签不可以为空");
        }

        //补全数据 userId,userAvatar，useName 创建更新时间
        article.setId(idWorker.nextId() + "");
        article.setUserId(blogUser.getId());
        article.setUserAvatar(blogUser.getAvatar());
        article.setUseName(blogUser.getUserName());
        article.setCreateTime(new Date());
        article.setUpdateTime(new Date());

        //保存数据 最好try一下 要么检查写完整
        articleDao.save(article);
        //todo 将搜索的关键字保存到数据库中
        return ResponseResult.SUCCESS("文章保存成功");
    }
}

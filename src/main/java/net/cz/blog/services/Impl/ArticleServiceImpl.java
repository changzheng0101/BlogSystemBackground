package net.cz.blog.services.Impl;

import net.cz.blog.Dao.ArticleDao;
import net.cz.blog.Dao.ArticleNoContentDao;
import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.Article;
import net.cz.blog.pojo.ArticleNoContent;
import net.cz.blog.pojo.BlogUser;
import net.cz.blog.services.IArticleService;
import net.cz.blog.services.IUserService;
import net.cz.blog.utils.Constants;
import net.cz.blog.utils.SnowflakeIdWorker;
import net.cz.blog.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

@Service
@Transactional
public class ArticleServiceImpl extends BaseService implements IArticleService {

    @Autowired
    private IUserService userService;
    @Autowired
    private SnowflakeIdWorker idWorker;
    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private ArticleNoContentDao articleNoContentDao;

    /**
     * 后期考虑定时发布的功能
     * 如果是多人博客系统，得考虑审核的问题-->成功或不成功 都通知
     * <p>
     * 保存成草稿
     * 1. 用户自己保存-->跳转到新的页面
     * 2.系统自动保存 -->每隔一段时间自动保存 如果没有唯一标识  就添加到数据库中
     * <p>
     * 方案一：每次用户发文章，向后台请求一个id
     * 更新文章，就不用请求这个id
     * <p>
     * 方案二：直接提交，后台判断有无id，如果没有创建id，并且返回id
     * 如果有，修改已经存在的内容
     * <p>
     * 放置重复提交-网络不好的时候，用户多次点击提交
     * 获得唯一的id
     * 通过token--key的方式，判断用户，30s内只允许提交一次
     * <p>
     * 前端：点击按钮之后，按钮被禁用，等后端有返回结果之后才可以继续使用
     *
     * @param article
     * @return
     */
    @Override
    public ResponseResult postArticle(Article article) {
        //只支持两个功能 发布文章和草稿
        String articleId = article.getId();
        String state = article.getState();
        if (TextUtils.isEmpty(state)) {
            return ResponseResult.FAILED("文章类型不可以为空");
        }
        if (!Constants.Article.ARTICLE_PUBLISH.equals(state) &&
                !Constants.Article.ARTICLE_DRAFT.equals(state)) {
            return ResponseResult.FAILED("文章类型不支持");
        }
        BlogUser blogUser = userService.checkBolgUser();
        if (Constants.Article.ARTICLE_PUBLISH.equals(state)) {
            //检查数据 标题、分类、内容、类型、摘要、标签
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
                return ResponseResult.FAILED("文章类型不可以为空");
            }
            if (!"0".equals(type) &&
                    !"1".equals(type)) {
                return ResponseResult.FAILED("文章类型不支持");
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
        }

        if (TextUtils.isEmpty(articleId)) {
            //补全数据 userId,userAvatar，useName 创建更新时间
            articleId = idWorker.nextId() + "";
            article.setId(articleId);
            article.setUserAvatar(blogUser.getAvatar());
            article.setUseName(blogUser.getUserName());
            article.setCreateTime(new Date());
            //保存数据 最好try一下 要么检查写完整
        } else {
            //有id 一定是想保存草稿
            //如果是已经发布的，就不能再保存为草稿
            Article articleFromDb = articleDao.findOneById(articleId);
            if (Constants.Article.ARTICLE_PUBLISH.equals(articleFromDb.getState()) &&
                    Constants.Article.ARTICLE_DRAFT.equals(state)) {
                return ResponseResult.FAILED("已经发布的不能再保存为草稿");
            }
        }
        article.setUserId(blogUser.getId());
        article.setUpdateTime(new Date());
        articleDao.save(article);

        //todo 将搜索的关键字保存到数据库中
        return ResponseResult.SUCCESS(Constants.Article.ARTICLE_PUBLISH.equals(state)
                ? "文章发表成功" : "草稿发布成功").setData(articleId);
    }

    @Override
    public ResponseResult getArticleList(int page, int size, String state, String categoryId, String keyword) {
        //处理数据
        page = checkPage(page);
        size = checkSize(size);
        //sort
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        //查询
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<ArticleNoContent> all = articleNoContentDao.findAll(new Specification<ArticleNoContent>() {
            @Override
            public Predicate toPredicate(Root<ArticleNoContent> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                //进行条件查询
                List<Predicate> predicates = new ArrayList<>();
                if (!TextUtils.isEmpty(state)) {
                    Predicate statePre = criteriaBuilder.equal(root.get("state").as(String.class), state);
                    predicates.add(statePre);
                }
                if (!TextUtils.isEmpty(categoryId)) {
                    Predicate categoryIdPre = criteriaBuilder.equal(root.get("categoryId").as(String.class), categoryId);
                    predicates.add(categoryIdPre);
                }
                if (!TextUtils.isEmpty(keyword)) {
                    Predicate keywordPre = criteriaBuilder.like(root.get("title").as(String.class), "%" + keyword + "%");
                    predicates.add(keywordPre);
                }
                Predicate[] preArray = new Predicate[predicates.size()];
                return criteriaBuilder.and(predicates.toArray(preArray));
            }
        }, pageable);
        //返回结果
        return ResponseResult.SUCCESS("条件查询成功").setData(all);
    }

    /**
     * 如果有审核机制--审核中的管理员和用户才可以看
     * 删除、草稿、已发布、置顶
     * 所有人都可以访问已发布和置顶
     * 只有管理员可以访问草稿和删除的
     * 草稿用户本人也可以进行访问
     * <p>
     * 目前是单人博客 草稿和删除的直接管理员权限才可以访问
     *
     * @param articleId 文章id
     * @return
     */
    @Override
    public ResponseResult getArticle(String articleId) {
        Article articleFromDb = articleDao.findOneById(articleId);
        if (articleFromDb == null) {
            return ResponseResult.FAILED("文章不存在");
        }
        String state = articleFromDb.getState();
        if (Constants.Article.ARTICLE_PUBLISH.equals(state) ||
                Constants.Article.ARTICLE_TOP.equals(state)) {
            return ResponseResult.SUCCESS("文章查询成功").setData(articleFromDb);
        }
        //鉴权
        BlogUser blogUser = userService.checkBolgUser();
        if (blogUser == null || !Constants.User.ROLE_ADMIN.equals(blogUser.getRoles())) {
            return ResponseResult.PERMISSION_FORBID();
        }
        return ResponseResult.SUCCESS("文章查询成功").setData(articleFromDb);
    }

    @Override
    public ResponseResult updateArticle(String articleId, Article article) {
        Article articleFromDb = articleDao.findOneById(articleId);
        if (articleFromDb == null) {
            return ResponseResult.FAILED("文章不存在");
        }
        //可更新：标题 分类id 内容 cover 摘要 标签
        String title = article.getTitle();
        if (!TextUtils.isEmpty(title)) {
            articleFromDb.setTitle(title);
        }
        String categoryId = article.getCategoryId();
        if (!TextUtils.isEmpty(categoryId)) {
            articleFromDb.setTitle(categoryId);
        }
        String content = article.getContent();
        if (!TextUtils.isEmpty(content)) {
            articleFromDb.setTitle(content);
        }
        String cover = article.getCover();
        if (!TextUtils.isEmpty(cover)) {
            articleFromDb.setTitle(cover);
        }
        String summary = article.getSummary();
        if (!TextUtils.isEmpty(summary)) {
            articleFromDb.setTitle(summary);
        }
        String labels = article.getLabels();
        if (TextUtils.isEmpty(labels)) {
            articleFromDb.setLabels(labels);
        }
        articleFromDb.setUpdateTime(new Date());
        articleDao.save(articleFromDb);
        return ResponseResult.SUCCESS("文章修改成功");
    }

    @Override
    public ResponseResult deleteArticleById(String articleId) {
        int result = articleDao.deleteAlLById(articleId);
        if (result > 0) {
            return ResponseResult.SUCCESS("文章删除成功");
        }
        return ResponseResult.FAILED("文章不存在，删除失败");
    }

    /**
     * 置顶文章，要判断状态 草稿和删除的不能置顶
     * 已经置顶的取消置顶
     *
     * @param articleId
     * @return
     */
    @Override
    public ResponseResult topArticle(String articleId) {
        Article articleFromDb = articleDao.findOneById(articleId);
        if (articleFromDb == null) {
            return ResponseResult.FAILED("文章不存在，置顶失败");
        }
        String state = articleFromDb.getState();
        switch (state) {
            case Constants.Article.ARTICLE_PUBLISH:
                articleFromDb.setState(Constants.Article.ARTICLE_TOP);
                articleDao.save(articleFromDb);
                return ResponseResult.SUCCESS("文章置顶成功");
            case Constants.Article.ARTICLE_DRAFT:
            case Constants.Article.ARTICLE_DELETE:
                return ResponseResult.FAILED("文章状态有误");
            case Constants.Article.ARTICLE_TOP:
                articleFromDb.setState(Constants.Article.ARTICLE_PUBLISH);
                articleDao.save(articleFromDb);
                return ResponseResult.SUCCESS("文章取消置顶成功");
        }
        return ResponseResult.FAILED("文章状态不在已经给定的状态中");
    }

    @Override
    public ResponseResult deleteArticleByChangeState(String articleId) {
        int result = articleDao.deleteAllByChangeState(articleId);
        if (result > 0) {
            return ResponseResult.SUCCESS("文章删除成功");
        }
        return ResponseResult.FAILED("文章不存在，删除失败");
    }

    @Override
    public ResponseResult getTopArticleList() {
        List<Article> result = articleDao.findAll(new Specification<Article>() {
            @Override
            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("state").as(String.class), Constants.Article.ARTICLE_TOP);
            }
        });
        return ResponseResult.SUCCESS("获取置顶文章成功").setData(result);
    }

    @Autowired
    private Random random;

    @Override
    public ResponseResult getRecommendArticles(String articleId, int size) {
        //查询文章 只需要标签 无需文章
        String labels = articleDao.getArticleLabelsById(articleId);
        //打散标签
        List<String> labelList = new ArrayList<>();
        if (!labels.contains("-")) {
            labelList.add(labels);
        } else {
            labelList.addAll(Arrays.asList(labels.split("-")));
        }
        //随机取一个标签
        String randomLabel = labelList.get(random.nextInt(labelList.size()));
        List<ArticleNoContent> articleListByLabel = articleNoContentDao.getArticleListByLabel(randomLabel, articleId, size);
        if (articleListByLabel.size() < size) {
            //没找够继续找
            //todo 有一定弊端 可能把已经找过的也加进来 但是在文章多的时候概率很小
            List<ArticleNoContent> lastedArticleListBySize =
                    articleNoContentDao.getLastedArticleListBySize(articleId, size - articleListByLabel.size());
            articleListByLabel.addAll(lastedArticleListBySize);
        }
        return ResponseResult.SUCCESS("获取推荐文章成功").setData(articleListByLabel);
    }
}

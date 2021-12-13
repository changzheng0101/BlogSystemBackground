package net.cz.blog.services.Impl;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.cz.blog.Dao.ArticleDao;
import net.cz.blog.Dao.ArticleNoContentDao;
import net.cz.blog.Dao.CommentDao;
import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.ArticleNoContent;
import net.cz.blog.pojo.BlogUser;
import net.cz.blog.pojo.Comment;
import net.cz.blog.pojo.PageList;
import net.cz.blog.services.ICommentService;
import net.cz.blog.services.IUserService;
import net.cz.blog.utils.Constants;
import net.cz.blog.utils.RedisUtil;
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
import java.util.Date;

@Service
@Transactional
public class CommentServiceImpl extends BaseService implements ICommentService {

    @Autowired
    private IUserService userService;
    @Autowired
    private ArticleNoContentDao articleNoContentDao;
    @Autowired
    private SnowflakeIdWorker idWorker;
    @Autowired
    private CommentDao commentDao;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private Gson gson;

    @Override
    public ResponseResult postComment(Comment comment) {
        //检查用户是否登录
        BlogUser blogUser = userService.checkBolgUser();
        if (blogUser == null) {
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }
        //检查数据
        String articleId = comment.getArticleId();
        if (TextUtils.isEmpty(articleId)) {
            return ResponseResult.FAILED("文章id不可以为空");
        }
        ArticleNoContent article = articleNoContentDao.findOneById(articleId);
        if (article == null) {
            return ResponseResult.FAILED("您要评论的文章不存在");
        }
        String content = comment.getContent();
        if (content == null) {
            return ResponseResult.FAILED("文章评论内容不可以为空");
        }
        //补全数据
        comment.setId(String.format("%d", idWorker.nextId()));
        comment.setUserId(blogUser.getId());
        comment.setUserAvatar(blogUser.getAvatar());
        comment.setUserName(blogUser.getUserName());
        comment.setCreateTime(new Date());
        comment.setUpdateTime(new Date());
        //保存数据
        commentDao.save(comment);
        return ResponseResult.SUCCESS("评论成功");
    }

    /**
     * 评论策略：
     * 1. 根据时间进行排序
     * 2. 发表的N段时间内，该评论在前面，一段时间之后，按照点赞量和创建的时间进行排序
     * <p>
     * 根据文章id获取评论列表
     *
     * 对于第一页 从redis中获取，访问快，其余从mysql中进行获取
     *
     * @param articleId
     * @param page
     * @param size
     * @return
     */
    @Override
    public ResponseResult listCommentByArticleId(String articleId, int page, int size) {
        page = checkPage(page);
        size = checkSize(size);
        if (page==1) {
            //从redis中拿到的是自己写的pageList
            String commentsCacheJson = (String) redisUtil.get(Constants.Comment.KEY_FIRST_PAGE_COMMENTS + articleId);
            if (!TextUtils.isEmpty(commentsCacheJson)) {
                PageList<Comment> result = gson.fromJson(commentsCacheJson, new TypeToken<PageList<Comment>>() {
                }.getType());
                return ResponseResult.SUCCESS("查询文章评论列表成功").setData(result);
            }
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Comment> result = commentDao.findAll(new Specification<Comment>() {
            @Override
            public Predicate toPredicate(Root<Comment> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("article_id").as(String.class), articleId);
            }
        }, pageable);
        //把结果转化为pageList
        PageList<Comment> commentPageList = new PageList<>();
        commentPageList.parsePage(result);
        //把第一页保存到redis中
        if (page==1) {
            redisUtil.set(Constants.Comment.KEY_FIRST_PAGE_COMMENTS + articleId, gson.toJson(commentPageList),
                    Constants.TimeValue.MIN*10);
        }
        return ResponseResult.SUCCESS("查询文章评论列表成功").setData(commentPageList);
    }

    @Override
    public ResponseResult deleteCommentById(String commentId) {
        //检查用户是否登录
        BlogUser blogUser = userService.checkBolgUser();
        if (blogUser == null) {
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }
        // 获取评论
        Comment comment = commentDao.findOneById(commentId);
        if (comment == null) {
            return ResponseResult.FAILED("评论不存在");
        }
        //判断角色
        if (comment.getUserId().equals(blogUser.getId()) ||
                Constants.User.ROLE_ADMIN.equals(blogUser.getRoles())) {
            //自己的可以删除 管理员--可以删除所有的
            commentDao.deleteById(commentId);
            return ResponseResult.SUCCESS("评论删除成功");
        }
        return ResponseResult.FAILED("评论删除失败");
    }

    @Override
    public ResponseResult getCommentList(int page, int size) {
        page = checkPage(page);
        size = checkSize(size);
        Sort sort = Sort.by(Sort.Direction.DESC, "state", "createTime");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Comment> result = commentDao.findAll(pageable);
        return ResponseResult.SUCCESS("评论列表查询成功").setData(result);
    }

    @Override
    public ResponseResult topComment(String commentId) {
        Comment comment = commentDao.findOneById(commentId);
        if (comment == null) {
            return ResponseResult.FAILED("评论不存在");
        }
        String state = comment.getState();
        if (Constants.Comment.ARTICLE_PUBLISH.equals(state)) {
            comment.setState(Constants.Comment.ARTICLE_TOP);
            return ResponseResult.SUCCESS("置顶成功");
        } else if (Constants.Comment.ARTICLE_TOP.equals(state)) {
            comment.setState(Constants.Comment.ARTICLE_PUBLISH);
            return ResponseResult.SUCCESS("取消置顶成功");
        } else {
            return ResponseResult.FAILED("不支持此操作");
        }
    }

}

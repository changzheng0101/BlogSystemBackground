package net.cz.blog.controller.portal;


import net.cz.blog.Response.ResponseResult;
import net.cz.blog.interceptor.checkTooFrequentCommit;
import net.cz.blog.pojo.Comment;
import net.cz.blog.services.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portal/comment")
public class CommentPortalApi {

    @Autowired
    private ICommentService commentService;

    @checkTooFrequentCommit
    @PostMapping
    public ResponseResult postComment(@RequestBody Comment comment) {
        return commentService.postComment(comment);
    }

    @DeleteMapping("/{CommentId}")
    public ResponseResult deleteComment(@PathVariable("CommentId") String CommentId) {
        return commentService.deleteCommentById(CommentId);
    }

    @PutMapping("/{CommentId}")
    public ResponseResult updateComment(@PathVariable("CommentId") String CommentId) {
        return null;
    }

    //    根据文章获取评论列表
    @GetMapping("/list/{articleId}/{page}/{size}")
    public ResponseResult listComments(@PathVariable("articleId") String articleId,
                                       @PathVariable("page") int page,
                                       @PathVariable("size") int size) {
        return commentService.listCommentByArticleId(articleId,page,size);
    }
}

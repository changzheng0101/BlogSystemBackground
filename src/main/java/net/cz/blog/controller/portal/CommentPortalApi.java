package net.cz.blog.controller.portal;


import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.Comment;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portal/comment")
public class CommentPortalApi {
    @PostMapping
    public ResponseResult postComment(@RequestBody Comment comment) {
        return null;
    }

    @DeleteMapping("/{CommentId}")
    public ResponseResult deleteComment(@PathVariable("CommentId") String CommentId) {
        return null;
    }

    @PutMapping("/{CommentId}")
    public ResponseResult updateComment(@PathVariable("CommentId") String CommentId) {
        return null;
    }

    //    根据文章获取评论列表
    @GetMapping("/list/{articleId}")
    public ResponseResult listComments(@PathVariable("articleId") String articleId) {
        return null;
    }
}

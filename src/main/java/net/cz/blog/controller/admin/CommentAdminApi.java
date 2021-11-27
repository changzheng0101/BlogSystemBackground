package net.cz.blog.controller.admin;

import net.cz.blog.Response.ResponseResult;
import net.cz.blog.services.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/comment")
public class CommentAdminApi {
    //增删改查 还有列出所有数据的方法
    @Autowired
    private ICommentService commentService;

    @PreAuthorize("@permission.isAdmin()")
    @DeleteMapping("/{commentId}")
    public ResponseResult deleteComment(@PathVariable("commentId") String commentId) {
        return commentService.deleteCommentById(commentId);
    }


    @PreAuthorize("@permission.isAdmin()")
    @GetMapping("/list")
    public ResponseResult getCommentList(@RequestParam("page") int page, @RequestParam("size") int size) {
        return commentService.getCommentList(page,size);
    }

    @PreAuthorize("@permission.isAdmin()")
    @PutMapping("/top/{commentId}")
    public ResponseResult topComment(@PathVariable("commentId") String commentId) {
        return commentService.topComment(commentId);
    }

}

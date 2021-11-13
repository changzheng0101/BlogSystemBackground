package net.cz.blog.controller.admin;

import net.cz.blog.Response.ResponseResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/comment")
public class CommentAdminApi {
    //增删改查 还有列出所有数据的方法

    @DeleteMapping("/{commentId}")
    public ResponseResult deleteComment(@PathVariable("commentId") String commentId) {
        return null;
    }

    @PutMapping("/{commentId}")
    public ResponseResult updateComment(@PathVariable("commentId") String commentId) {
        return null;
    }

    @GetMapping("/{commentId}")
    public ResponseResult getComment(@PathVariable("commentId") String commentId) {
        return null;
    }

    @GetMapping("/list")
    public ResponseResult getCommentList(@RequestParam("page") int page, @RequestParam("size") int size) {
        return null;
    }

    @PutMapping("/top/{commentId}")
    public ResponseResult topComment(@PathVariable("commentId") String commentId){
        return null;
    }
}

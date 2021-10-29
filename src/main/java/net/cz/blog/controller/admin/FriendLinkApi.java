package net.cz.blog.controller.admin;

import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.FriendLink;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/friend_link")
public class FriendLinkApi {
    //增删改查 还有列出所有数据的方法
    @PostMapping
    public ResponseResult addFriendLink(@RequestBody FriendLink friendsLink) {
        return null;
    }

    @DeleteMapping("/{friendsLinkId}")
    public ResponseResult deleteFriendLink(@PathVariable("friendsLinkId") String friendsLinkId) {
        return null;
    }

    @PutMapping("/{friendsLinkId}")
    public ResponseResult updateFriendLink(@PathVariable("friendsLinkId") String friendsLinkId) {
        return null;
    }

    @GetMapping("/{friendsLinkId}")
    public ResponseResult getFriendLink(@PathVariable("friendsLinkId") String friendsLinkId) {
        return null;
    }

    @GetMapping("/list")
    public ResponseResult getFriendLinkList(@RequestParam("page") int page, @RequestParam("size") int size) {
        return null;
    }

}

package net.cz.blog.controller.admin;

import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.FriendLink;
import net.cz.blog.services.IFriendLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/friend_link")
public class FriendLinkAdminApi {

    @Autowired
    private IFriendLinkService friendLinkService;

    //增删改查 还有列出所有数据的方法
    @PreAuthorize("@permission.isAdmin()")
    @PostMapping
    public ResponseResult addFriendLink(@RequestBody FriendLink friendLink) {
        return friendLinkService.addFriendLink(friendLink);
    }

    @PreAuthorize("@permission.isAdmin()")
    @DeleteMapping("/{friendsLinkId}")
    public ResponseResult deleteFriendLink(@PathVariable("friendsLinkId") String friendsLinkId) {
        return friendLinkService.deleteFriendLink(friendsLinkId);
    }

    @PreAuthorize("@permission.isAdmin()")
    @PutMapping("/{friendsLinkId}")
    public ResponseResult updateFriendLink(@PathVariable("friendsLinkId") String friendsLinkId,
                                           @RequestBody FriendLink friendLink) {
        return friendLinkService.updateFriendLink(friendsLinkId, friendLink);
    }

    @PreAuthorize("@permission.isAdmin()")
    @GetMapping("/{friendsLinkId}")
    public ResponseResult getFriendLink(@PathVariable("friendsLinkId") String friendsLinkId) {
        return friendLinkService.getFriendLink(friendsLinkId);
    }

    @PreAuthorize("@permission.isAdmin()")
    @GetMapping("/list/{page}/{size}")
    public ResponseResult getFriendLinkList(@PathVariable("page") int page, @PathVariable("size") int size) {
        return friendLinkService.getFriendLinkList(page, size);
    }

}

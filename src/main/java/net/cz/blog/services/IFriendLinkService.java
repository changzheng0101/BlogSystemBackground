package net.cz.blog.services;

import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.FriendLink;

public interface IFriendLinkService {
    ResponseResult addFriendLink(FriendLink friendLink);

    ResponseResult getFriendLink(String friendsLinkId);

    ResponseResult getFriendLinkList(int page, int size);

    ResponseResult deleteFriendLink(String friendsLinkId);

    ResponseResult updateFriendLink(String friendsLinkId, FriendLink friendLink);
}

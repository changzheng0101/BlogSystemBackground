package net.cz.blog.services.Impl;

import net.cz.blog.Dao.FriendLinkDao;
import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.BlogUser;
import net.cz.blog.pojo.FriendLink;
import net.cz.blog.services.IFriendLinkService;
import net.cz.blog.services.IUserService;
import net.cz.blog.utils.Constants;
import net.cz.blog.utils.SnowflakeIdWorker;
import net.cz.blog.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class FriendLinkServiceImpl extends BaseService implements IFriendLinkService {

    @Autowired
    private SnowflakeIdWorker idWorker;
    @Autowired
    private FriendLinkDao friendLinkDao;

    @Override
    public ResponseResult addFriendLink(FriendLink friendLink) {
        //检查数据
        if (TextUtils.isEmpty(friendLink.getUrl())) {
            return ResponseResult.FAILED("URL不可以为空");
        }
        if (TextUtils.isEmpty(friendLink.getLogo())) {
            return ResponseResult.FAILED("logo不可以为空");
        }
        if (TextUtils.isEmpty(friendLink.getName())) {
            return ResponseResult.FAILED("对方网站名不可以为空");
        }
        //补全数据
        friendLink.setId(idWorker.nextId() + "");
        friendLink.setCreateTime(new Date());
        friendLink.setUpdateTime(new Date());

        //保存数据
        friendLinkDao.save(friendLink);
        return ResponseResult.SUCCESS("友情链接添加成功");
    }

    @Override
    public ResponseResult getFriendLink(String friendsLinkId) {
        FriendLink friendLink = friendLinkDao.findOneById(friendsLinkId);
        if (friendLink == null) {
            return ResponseResult.FAILED("该友情链接不存在");
        }
        return ResponseResult.SUCCESS("获取成功").setData(friendLink);
    }

    @Autowired
    private IUserService userService;

    @Override
    public ResponseResult getFriendLinkList() {
        BlogUser blogUser = userService.checkBolgUser();
        List<FriendLink> friendLinkList = new ArrayList<>();
        if (blogUser == null || !Constants.User.ROLE_ADMIN.equals(blogUser.getRoles())) {
            //处理其余情况 只能查询没有改变删除状态的
            friendLinkList = friendLinkDao.listFriendLinkByStatus("1");
        } else {
            //创建条件
            Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
            //查询
            friendLinkList = friendLinkDao.findAll(sort);
        }
        return ResponseResult.SUCCESS("查询列表成功").setData(friendLinkList);
    }

    @Override
    public ResponseResult deleteFriendLink(String friendsLinkId) {
        int result = friendLinkDao.deleteAllById(friendsLinkId);
        if (result == 0) {
            return ResponseResult.FAILED("友情链接不存在");
        }
        return ResponseResult.SUCCESS("删除成功");
    }

    @Override
    public ResponseResult updateFriendLink(String friendsLinkId, FriendLink friendLink) {
        FriendLink friendLinkFromDb = friendLinkDao.findOneById(friendsLinkId);
        if (friendLinkFromDb == null) {
            return ResponseResult.FAILED("友情链接不存在");
        }
        if (!TextUtils.isEmpty(friendLink.getLogo())) {
            friendLinkFromDb.setLogo(friendLink.getLogo());
        }
        if (!TextUtils.isEmpty(friendLink.getName())) {
            friendLinkFromDb.setName(friendLink.getName());
        }
        if (!TextUtils.isEmpty(friendLink.getUrl())) {
            friendLinkFromDb.setUrl(friendLink.getUrl());
        }
        if (!TextUtils.isEmpty(friendLink.getOrder() + "")) {
            friendLinkFromDb.setOrder(friendLink.getOrder());
        }
        friendLinkFromDb.setUpdateTime(new Date());
        friendLinkDao.save(friendLinkFromDb);
        return ResponseResult.SUCCESS("更新友情链接成功");
    }
}

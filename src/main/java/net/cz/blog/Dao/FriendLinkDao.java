package net.cz.blog.Dao;

import net.cz.blog.pojo.FriendLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FriendLinkDao extends JpaRepository<FriendLink, String>, JpaSpecificationExecutor<FriendLink> {
    FriendLink findOneById(String friendsLinkId);

    int deleteAllById(String friendsLinkId);

}

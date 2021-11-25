package net.cz.blog.Dao;

import net.cz.blog.pojo.FriendLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FriendLinkDao extends JpaRepository<FriendLink, String>, JpaSpecificationExecutor<FriendLink> {
    FriendLink findOneById(String friendsLinkId);

    int deleteAllById(String friendsLinkId);

    @Query(nativeQuery = true, value = "select  * from `tb_friends` where `state`= ? ")
    List<FriendLink> listFriendLinkByStatus(String state);
}

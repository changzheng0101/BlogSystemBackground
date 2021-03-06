package net.cz.blog.Dao;

import net.cz.blog.pojo.BlogUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserDao extends JpaRepository<BlogUser, String>, JpaSpecificationExecutor<String> {
    BlogUser findOneByUserName(String userName);

    BlogUser findOneByEmail(String email);

    BlogUser findOneById(String id);


    /**
     * 通过修改用户的state来删除用户
     *
     * @param userId
     * @return
     */
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE `tb_user` SET state = '0' WHERE id=?")
    int deleteBlogUserByState(String userId);


    @Modifying
    @Query(nativeQuery = true, value = "UPDATE `tb_user` SET `password` = ? WHERE `email`=?")
    int updatePasswordByEmail(String password, String email);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE `tb_user` SET `email` = ? WHERE `id`=?")
    int updateEmailById(String email, String id);
}

package net.cz.blog.Dao;

import net.cz.blog.pojo.BlogUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserDao extends JpaRepository<BlogUser, String>, JpaSpecificationExecutor<String> {
    BlogUser findOneByUserName(String userName);

    BlogUser findOneByEmail(String email);

    BlogUser findOneById(String id);
}

package net.cz.blog.Dao;

import net.cz.blog.pojo.BlogUserNoPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface UserNoPasswordDao extends
        JpaRepository<BlogUserNoPassword, String>, JpaSpecificationExecutor<BlogUserNoPassword> {
}

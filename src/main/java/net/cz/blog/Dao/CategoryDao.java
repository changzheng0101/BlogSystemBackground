package net.cz.blog.Dao;

import net.cz.blog.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CategoryDao extends JpaRepository<Category, String>, JpaSpecificationExecutor<Category> {
}

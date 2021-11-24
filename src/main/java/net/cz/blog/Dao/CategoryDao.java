package net.cz.blog.Dao;

import net.cz.blog.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryDao extends JpaRepository<Category, String>, JpaSpecificationExecutor<Category> {
    Category findOneById(String categoryId);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE `tb_categories` SET `status` = '0' WHERE `id`=?")
    int deleteCategoryByUpdateStatus(String categoryId);

    @Query(nativeQuery = true, value = "select * from `tb_categories` where `status`=? order by `create_time` DESC ")
    List<Category> listCategoryByStatus(String status);
}

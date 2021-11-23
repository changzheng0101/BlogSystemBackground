package net.cz.blog.Dao;

import net.cz.blog.pojo.Article;
import net.cz.blog.pojo.ArticleNoContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ArticleNoContentDao extends JpaRepository<ArticleNoContent, String>,
        JpaSpecificationExecutor<ArticleNoContent> {
    ArticleNoContent findOneById(String id);
}

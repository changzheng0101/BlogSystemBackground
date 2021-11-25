package net.cz.blog.Dao;

import net.cz.blog.pojo.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ArticleDao extends JpaRepository<Article, String>, JpaSpecificationExecutor<Article> {
    Article findOneById(String id);

    @Modifying
    int deleteAlLById(String id);

    @Modifying
    @Query(nativeQuery = true, value = "update `tb_user` set `state`='0' where `id`=?")
    int deleteAllByChangeState(String id);

    @Query(nativeQuery = true, value = "select `labels` from `tb_article` where `id`=?")
    String getArticleLabelsById(String articleId);


}

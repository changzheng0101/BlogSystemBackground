package net.cz.blog.Dao;

import net.cz.blog.pojo.Article;
import net.cz.blog.pojo.ArticleNoContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ArticleNoContentDao extends JpaRepository<ArticleNoContent, String>,
        JpaSpecificationExecutor<ArticleNoContent> {
    ArticleNoContent findOneById(String id);


    @Query(nativeQuery = true, value = "select * from `tb_article` where `labels` like ? and `id` != ?" +
            " and (`state`='1' or `state`='3') LIMIT ?")
    List<ArticleNoContent> getArticleListByLabel(String randomLabel, String articleId, int size);

    @Query(nativeQuery = true, value = "select * from `tb_article` where  `id` != ? and (`state`='1' or `state`='3')" +
            " order by `create_time` DESC LIMIT ?")
    List<ArticleNoContent> getLastedArticleListBySize(String articleId, int size);


    @Query(nativeQuery = true, value = "select  * from `tb_article` where `state`=?  order by `create_time`")
    List<ArticleNoContent> findAllByState(String articleSelected);

}

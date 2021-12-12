package net.cz.blog.services.Impl;

import com.vladsch.flexmark.ext.jekyll.tag.JekyllTagExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.toc.SimTocExtension;
import com.vladsch.flexmark.ext.toc.TocExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import net.cz.blog.Dao.ArticleDao;
import net.cz.blog.pojo.Article;
import net.cz.blog.utils.Constants;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class SolrTestService {

    @Autowired
    private SolrClient solrClient;
    @Autowired
    private ArticleDao articleDao;

    public void importAll() {
        List<Article> all = articleDao.findAll();
        for (Article article : all) {
            if (Constants.Article.ARTICLE_DRAFT.equals(article.getState()) ||
                    Constants.Article.ARTICLE_DELETE.equals(article.getState())) {
                continue;
            }
            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", article.getId());
            doc.addField("blog_view_count", article.getViewCount());
            doc.addField("blog_title", article.getTitle());
            //对内容进行处理，去掉标签，提取出纯文本
            //第一种是由markdown写的内容--->type = 1
            //第二种是富文本内容 === > type = 0
            //如果type === 1 ===> 转成html
            //再由html === > 纯文本
            //如果type == 0 == > 纯文本
            String type = article.getType();
            String html;
            if (Constants.Article.TYPE_MARKDOWN.equals(type)) {
                //转成html
                // markdown to html
                MutableDataSet options = new MutableDataSet().set(Parser.EXTENSIONS, Arrays.asList(
                        TablesExtension.create(),
                        JekyllTagExtension.create(),
                        TocExtension.create(),
                        SimTocExtension.create()
                ));
                Parser parser = Parser.builder(options).build();
                HtmlRenderer renderer = HtmlRenderer.builder(options).build();
                Node document = parser.parse(article.getContent());
                html = renderer.render(document);
            } else {
                html = article.getContent();
            }
            //到这里,不管原来是什么,现在都是Html
            //html== > text
            String content = Jsoup.parse(html).text();
            doc.addField("blog_content", content);
            doc.addField("blog_create_time", article.getCreateTime());
            doc.addField("blog_labels", article.getLabels());
            doc.addField("blog_url", "/article/" + article.getId());
            doc.addField("blog_category_id", article.getCategoryId());
            try {
                solrClient.add(doc);
                solrClient.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

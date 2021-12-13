package net.cz.blog.services.Impl;

import com.vladsch.flexmark.ext.jekyll.tag.JekyllTagExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.toc.SimTocExtension;
import com.vladsch.flexmark.ext.toc.TocExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import lombok.extern.slf4j.Slf4j;
import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.Article;
import net.cz.blog.pojo.PageList;
import net.cz.blog.pojo.SearchResult;
import net.cz.blog.services.ISolrService;
import net.cz.blog.utils.Constants;
import net.cz.blog.utils.TextUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SolrServiceImpl extends BaseService implements ISolrService {

    @Autowired
    private SolrClient solrClient;

    @Override
    public ResponseResult doSearch(String keyword, int page, int size, String categoryId, Integer sort) {
        //1.检查page和size
        page = checkPage(page);
        size = checkSize(size);
        SolrQuery solrQuery = new SolrQuery();
        //2.分页设置
        solrQuery.setRows(size);
        int start = (page - 1) * size;
//        solrQuery.setStart(start); //也可以
        solrQuery.set("start", start);

        //3.设置搜索条件
        solrQuery.set("df", "search_item");
        //条件过滤
        if (TextUtils.isEmpty(keyword)) {
            solrQuery.set("q", "*");
        } else {
            solrQuery.set("q", keyword);
        }
        //设置分类id
        if (!TextUtils.isEmpty(categoryId)) {
            solrQuery.setFilterQueries("bolg_category_id:" + categoryId);
        }
        //排序 4种 按照时间降序(0)和升序(1)排列 按照浏览量降序(3)和升序(4)排列
        if (sort != null) {
            if (sort == 1) {
                solrQuery.setSort("blog_create_time", SolrQuery.ORDER.desc);
            } else if (sort == 2) {
                solrQuery.setSort("blog_create_time", SolrQuery.ORDER.asc);
            } else if (sort == 3) {
                solrQuery.setSort("blog_view_count", SolrQuery.ORDER.desc);
            } else if (sort == 4) {
                solrQuery.setSort("blog_view_count", SolrQuery.ORDER.asc);
            }
        }
        //关键字高亮
        solrQuery.setHighlight(true);
        solrQuery.addHighlightField("blog_title,blog_content");
        solrQuery.setHighlightSimplePre("<font color='red'>");
        solrQuery.setHighlightSimplePost("</font>");
        solrQuery.setHighlightFragsize(500); //设置高亮部分有多少个字符
        //设置返回结果
        solrQuery.addField("id,blog_content,blog_create_time,blog_labels,blog_url,blog_title,blog_view_count");
        //4.处理搜索结果
        try {
            QueryResponse result = solrClient.query(solrQuery);
            //获取高亮内容
            Map<String, Map<String, List<String>>> highlighting = result.getHighlighting();
            //把数据转化成beans
            List<SearchResult> resultList = result.getBeans(SearchResult.class);
            log.info(highlighting.toString());
            log.info(resultList.toString());
            for (SearchResult item : resultList) {
                Map<String, List<String>> stringListMap = highlighting.get(item.getId());
                if (stringListMap != null) {
                    List<String> blogTitle = stringListMap.get("blog_title");
                    if (blogTitle == null) {
                        item.setBlogTitle(blogTitle.get(0));
                    }
                    List<String> blogContent = stringListMap.get("blog_content");
                    if (blogContent != null) {
                        item.setBlogContent(blogContent.get(0));
                    }
                }
            }


            long numFound = result.getResults().getNumFound();
            PageList<SearchResult> pageList = new PageList<>(page, numFound, size);
            pageList.setContents(resultList);

            //5.返回搜索结果
            return ResponseResult.SUCCESS("搜索成功").setData(pageList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.FAILED("搜索失败，请稍后重试");
    }

    public void addArticle(Article article) {
        if (Constants.Article.ARTICLE_DRAFT.equals(article.getState()) ||
                Constants.Article.ARTICLE_DELETE.equals(article.getState())) {
            return;
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

    @Override
    public void deleteArticle(String articleId) {
        try {
            solrClient.deleteById(articleId);
            solrClient.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void updateArticle(String articleId, Article article) {
        article.setId(articleId);
        addArticle(article);
    }
}

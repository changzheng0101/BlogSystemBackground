package net.cz.blog.services.Impl;

import lombok.extern.slf4j.Slf4j;
import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.PageList;
import net.cz.blog.pojo.SearchResult;
import net.cz.blog.services.ISolrService;
import net.cz.blog.utils.TextUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        //设置返回结果
        solrQuery.addField("blog_content,blog_create_time,blog_labels,blog_url,blog_title,blog_view_count");
        //4.处理搜索结果
        try {
            QueryResponse result = solrClient.query(solrQuery);
            //获取高亮内容
            Map<String, Map<String, List<String>>> highlighting = result.getHighlighting();
            //把数据转化成beans
            List<SearchResult> resultList = result.getBeans(SearchResult.class);
            log.info(highlighting.toString());
            log.info(resultList.toString());

            PageList<SearchResult> pageList = new PageList<>();
            pageList.setContents(resultList);
            long numFound = result.getResults().getNumFound();
            pageList.setTotalCount(numFound);
            pageList.setPageSize(size);
            pageList.setCurrentPage(page);
            long totalPage = numFound / size;
            pageList.setTotalPage(totalPage);
            boolean isFirst = page == 1;
            pageList.setFirst(isFirst);
            boolean isLast = page == totalPage;
            pageList.setLast(isLast);
            //5.返回搜索结果
            return ResponseResult.SUCCESS("搜索成功").setData(pageList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.FAILED("搜索失败，请稍后重试");
    }
}

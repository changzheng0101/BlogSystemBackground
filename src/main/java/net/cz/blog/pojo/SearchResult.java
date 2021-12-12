package net.cz.blog.pojo;

import org.apache.solr.client.solrj.beans.Field;

import java.io.Serializable;
import java.util.Date;

public class SearchResult implements Serializable {
    //blog_content,blog_create_time,blog_labels,blog_url,blog_title,blog_view_count
    @Field("blog_content")
    private String blogContent;
    @Field("blog_create_time")
    private Date blogCreateTime;
    @Field("blog_labels")
    private String blogLabels;
    @Field("blog_url")
    private String blogUrl;
    @Field("blog_title")
    private String blogTitle;
    @Field("blog_view_count")
    private int blogViewCount;

    public String getBlogContent() {
        return blogContent;
    }

    public void setBlogContent(String blogContent) {
        this.blogContent = blogContent;
    }

    public Date getBlogCreateTime() {
        return blogCreateTime;
    }

    public void setBlogCreateTime(Date blogCreateTime) {
        this.blogCreateTime = blogCreateTime;
    }

    public String getBlogLabels() {
        return blogLabels;
    }

    public void setBlogLabels(String blogLabels) {
        this.blogLabels = blogLabels;
    }

    public String getBlogUrl() {
        return blogUrl;
    }

    public void setBlogUrl(String blogUrl) {
        this.blogUrl = blogUrl;
    }

    public String getBlogTitle() {
        return blogTitle;
    }

    public void setBlogTitle(String blogTitle) {
        this.blogTitle = blogTitle;
    }

    public int getBlogViewCount() {
        return blogViewCount;
    }

    public void setBlogViewCount(int blogViewCount) {
        this.blogViewCount = blogViewCount;
    }
}

package net.cz.blog.pojo;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tb_article")
public class Article {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "title")
    private String title;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_avatar")
    private String userAvatar;

    @Column(name = "user_name")
    private String useName;

    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "content")
    private String content;

    // 0 富文本 1 markdown
    @Column(name = "type")
    private String type;

    @Column(name = "cover")
    private String cover;

    // 0 删除 1 发布成功  2 草稿 3 置顶
    @Column(name = "state")
    private String state = "1";

    @Column(name = "summary")
    private String summary;


    //格式： 标签1-标签2-标签3...
    @Column(name = "labels")
    private String labels;

    @Column(name = "view_count")
    private long viewCount = 0L;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    @OneToOne(targetEntity = BlogUserNoPassword.class)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private BlogUserNoPassword blogUser;

    @Transient
    private List<String> labelResult = new ArrayList<>();

    public BlogUserNoPassword getBlogUser() {
        return blogUser;
    }

    public void setBlogUser(BlogUserNoPassword blogUser) {
        this.blogUser = blogUser;
    }

    public List<String> getLabelResult() {
        if (labels != null) {
            labelResult.clear();
            //换一种形式返回
            if (labels.contains("-")){
                labelResult.add(labels);
            }else {
                String[] split = labels.split("-");
                List<String> strings = Arrays.asList(split);
                labelResult.addAll(strings);
            }
        }
        return labelResult;
    }

    public void setLabelResult(List<String> labelResult) {
        this.labelResult = labelResult;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getUseName() {
        return useName;
    }

    public void setUseName(String useName) {
        this.useName = useName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getLabels() {

        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public long getViewCount() {
        return viewCount;
    }

    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}

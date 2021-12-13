package net.cz.blog.pojo;

import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

//将搜索的结果转化成分页数据
public class PageList<T> implements Serializable {

    public PageList() {

    }


    public PageList(long currentPage, long totalCount, long pageSize) {
        this.currentPage = currentPage;
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        //总页码判断
        if (totalCount % pageSize == 0) {
            totalPage = totalCount / pageSize;
        } else {
            totalPage = totalCount / pageSize + 1;
        }
        isFirst = currentPage == 1;
        isLast = currentPage == totalPage;
    }

    //数据
    //当前页码
    private long currentPage;
    //总数量
    private long totalCount;
    //每一页有多少数量
    private long pageSize;
    //总页数
    private long totalPage;
    //是否是第一页
    private boolean isFirst;
    //是否是最后一页
    private boolean isLast;

    private List<T> contents;

    public long getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(long currentPage) {
        this.currentPage = currentPage;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(long totalPage) {
        this.totalPage = totalPage;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }

    public List<T> getContents() {
        return contents;
    }

    public void setContents(List<T> contents) {
        this.contents = contents;
    }

    public void parsePage(Page<T> data){
        setContents(data.getContent());
        setFirst(data.isFirst());
        setLast(data.isLast());
        setCurrentPage(data.getNumber() + 1);
        setTotalPage(data.getTotalPages());
        setPageSize(data.getSize());
    }
}

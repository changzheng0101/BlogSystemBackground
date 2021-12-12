package net.cz.blog.services;

import net.cz.blog.Response.ResponseResult;

public interface ISolrService {
    ResponseResult doSearch(String keyword, int page, int size, String categoryId, Integer sort);
}

package net.cz.blog.controller.portal;

import net.cz.blog.Response.ResponseResult;
import net.cz.blog.services.ISolrService;
import net.cz.blog.services.Impl.SolrTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portal/search")
public class SearchPortalApi {

    @Autowired
    private ISolrService solrService;
    @Autowired
    private SolrTestService testService;

    @GetMapping
    public ResponseResult doSearch(@RequestParam("keyword") String keyword,
                                   @RequestParam("page") int page,
                                   @RequestParam("size") int size,
                                   @RequestParam(value = "categoryId", required = false) String categoryId,
                                   @RequestParam(value = "sort", required = false) Integer sort) {
        return solrService.doSearch(keyword, page, size, categoryId, sort);
    }

    @PutMapping("pushData")
    public ResponseResult pushData(){
        testService.importAll();
        return ResponseResult.SUCCESS("数据添加成功");
    }
}

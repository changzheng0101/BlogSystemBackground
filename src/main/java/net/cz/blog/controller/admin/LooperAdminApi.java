package net.cz.blog.controller.admin;

import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.Looper;
import net.cz.blog.services.ILooperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/looper")
public class LooperAdminApi {
    //增删改查 还有列出所有数据的方法

    @Autowired
    private ILooperService looperService;


    @PostMapping
    public ResponseResult addLooper(@RequestBody Looper looper) {
        return looperService.addLooper(looper);
    }

    @DeleteMapping("/{looperId}")
    public ResponseResult deleteLooper(@PathVariable("looperId") String looperId) {
        return null;
    }

    @PutMapping("/{looperId}")
    public ResponseResult updateLooper(@PathVariable("looperId") String looperId) {
        return null;
    }

    @GetMapping("/{looperId}")
    public ResponseResult getLooper(@PathVariable("looperId") String looperId) {
        return null;
    }

    @GetMapping("/list")
    public ResponseResult getLooperList(@RequestParam("page") int page, @RequestParam("size") int size) {
        return null;
    }
}

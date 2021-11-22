package net.cz.blog.controller.admin;

import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.Looper;
import net.cz.blog.services.ILooperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/looper")
public class LooperAdminApi {
    //增删改查 还有列出所有数据的方法

    @Autowired
    private ILooperService looperService;

    @PreAuthorize("@permission.isAdmin()")
    @PostMapping
    public ResponseResult addLooper(@RequestBody Looper looper) {
        return looperService.addLooper(looper);
    }

    @PreAuthorize("@permission.isAdmin()")
    @DeleteMapping("/{looperId}")
    public ResponseResult deleteLooper(@PathVariable("looperId") String looperId) {
        return looperService.deleteLooper(looperId);
    }

    @PreAuthorize("@permission.isAdmin()")
    @PutMapping("/{looperId}")
    public ResponseResult updateLooper(@PathVariable("looperId") String looperId, @RequestBody Looper looper) {
        return looperService.updateLooper(looperId, looper);
    }

    @PreAuthorize("@permission.isAdmin()")
    @GetMapping("/{looperId}")
    public ResponseResult getLooper(@PathVariable("looperId") String looperId) {
        return looperService.getLooper(looperId);
    }

    @PreAuthorize("@permission.isAdmin()")
    @GetMapping("/list/{page}/{size}")
    public ResponseResult getLooperList(@PathVariable("page") int page, @PathVariable("size") int size) {
        return looperService.getLooperList(page,size);
    }
}

package net.cz.blog.controller;


import lombok.extern.slf4j.Slf4j;
import net.cz.blog.Dao.LabelDao;
import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.House;
import net.cz.blog.pojo.User;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@Transactional
@Slf4j
@RestController  //不用声明 @ResponseBody -->这个代表返回的是json对象 不是一个页面
@RequestMapping("/test")  //代表所有文件都在test目录之下
public class TestController {

    @Autowired
    private LabelDao labelDao;

    @RequestMapping(value = "/hello-world", method = RequestMethod.GET)
    public ResponseResult helloWorld() {
        log.info("this is helloWorld from Spring boot");
        return ResponseResult.SUCCESS().setData("hello world");
    }

    @GetMapping("/test-json")
    public ResponseResult getJson() {
        log.info("this is json data test");
        User user = new User("json", 18);
        House house = new House("baiGong", "china");
        user.setHouse(house);
        return ResponseResult.FAILED().setData(user);
    }
}

package net.cz.blog.controller;


import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.extern.slf4j.Slf4j;
import net.cz.blog.Dao.LabelDao;
import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.House;
import net.cz.blog.pojo.Label;
import net.cz.blog.pojo.User;
import net.cz.blog.utils.Constants;
import net.cz.blog.utils.RedisUtil;
import net.cz.blog.utils.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.util.Date;
import java.util.List;

@Transactional
@Slf4j
@RestController  //不用声明 @ResponseBody -->这个代表返回的是json对象 不是一个页面
@RequestMapping("/test")  //代表所有文件都在test目录之下
public class TestController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private LabelDao labelDao;

    @Autowired
    private SnowflakeIdWorker idWorker;

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

    @PostMapping("/label")
    public ResponseResult addLabel(@RequestBody Label label) {
        //判断数据是否有效
        //添加数据
        label.setId(idWorker.nextId() + "");
        label.setCreateTime(new Date());
        label.setUpdateTime(new Date());

        //保存数据
        labelDao.save(label);
        return ResponseResult.SUCCESS("label数据保存成功");
    }


    @DeleteMapping("/label/{labelId}")
    public ResponseResult delLabel(@PathVariable("labelId") String labelId) {
        int delResult = labelDao.customDelLabelById(labelId);
        log.info("delResult-->" + delResult);
        if (delResult == 0) {
            return ResponseResult.FAILED("标签删除失败");
        }
        return ResponseResult.SUCCESS("删除标签成功");
    }

    @PutMapping("/label/{labelId}")
    public ResponseResult updateLabel(@PathVariable("labelId") String id, @RequestBody Label label) {
        Label dbLabel = labelDao.findOneById(id);
        if (dbLabel == null) {
            return ResponseResult.FAILED("标签不存在");
        }
        dbLabel.setCount(label.getCount());
        dbLabel.setName(label.getName());
        dbLabel.setUpdateTime(new Date());
        return ResponseResult.SUCCESS("标签修改成功");
    }

    @GetMapping("/label/{labelId}")
    public ResponseResult getLabel(@PathVariable("labelId") String id) {
        Label dbLabel = labelDao.findOneById(id);
        if (dbLabel == null) {
            return ResponseResult.FAILED("标签不存在");
        }
        return ResponseResult.SUCCESS("查找成功").setData(dbLabel);
    }

    @GetMapping("/page/list/{page}/{size}")
    public ResponseResult listLabels(@PathVariable("page") int page, @PathVariable("size") int size) {
        if (page < 1) {
            page = 1;
        }
        if (size <= 0) {
            size = Constants.DEFAULT_SIZE;
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Label> result = labelDao.findAll(pageable);
        return ResponseResult.SUCCESS("获取成功").setData(result);
    }

    //多条件查询
    @GetMapping("/label/search")
    public ResponseResult doLabelSearch(@RequestParam("keyword") String keyword, @RequestParam("count") int count) {
        List<Label> result = labelDao.findAll(new Specification<Label>() {
            @Override
            public Predicate toPredicate(Root<Label> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                //模糊匹配
                Predicate namePre = cb.like(root.get("name").as(String.class), "%" + keyword + "%");
                //精确查询
                Predicate countPre = cb.equal(root.get("count").as(Integer.class), count);
                Predicate finalPre = cb.and(namePre, countPre);
                return finalPre;
            }
        });
        if (result.size() == 0) {
            return ResponseResult.FAILED("结果为空");
        }
        return ResponseResult.SUCCESS("查询成功").setData(result);
    }

    //图灵验证码
    @RequestMapping("/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 设置请求头为输出图片类型
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        // 三个参数分别为宽、高、位数
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 5);
        // 设置字体
        specCaptcha.setFont(new Font("Verdana", Font.PLAIN, 32));  // 有默认字体，可以不用设置
        // 设置类型，纯数字、纯字母、字母数字混合
        specCaptcha.setCharType(Captcha.TYPE_DEFAULT);

        String content = specCaptcha.text().toLowerCase();
        log.info(content);
        // 验证码存入session 不太OK 进化一下保存到Redis
//        request.getSession().setAttribute("captcha", specCaptcha.text().toLowerCase());
        //保存到redis time代表数据有效时间
        redisUtil.set(Constants.User.KEY_CAPTCHA_CONTENT+"123",content,60*10);

        // 输出图片流
        specCaptcha.out(response.getOutputStream());
    }

}
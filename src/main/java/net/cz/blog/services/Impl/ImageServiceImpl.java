package net.cz.blog.services.Impl;

import lombok.extern.slf4j.Slf4j;
import net.cz.blog.Dao.ImageDao;
import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.BlogUser;
import net.cz.blog.pojo.Image;
import net.cz.blog.services.IImageService;
import net.cz.blog.services.IUserService;
import net.cz.blog.utils.Constants;
import net.cz.blog.utils.SnowflakeIdWorker;
import net.cz.blog.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.core.support.ReactiveRepositoryFactorySupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@Slf4j
@Service
@Transactional
public class ImageServiceImpl extends BaseService implements IImageService {


    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd");

    @Value("${blog.images.save-path}")
    private String imagePath;

    @Value("${blog.images.max-size}")
    private long maxSize;

    @Autowired
    private SnowflakeIdWorker idWorker;

    @Autowired
    private IUserService userService;

    @Autowired
    private ImageDao imageDao;

    /**
     * 上传路径：可以配置
     * 上传内容：命名 可以用ID
     * 每天一个文件夹
     * 保存数据到数据库
     *
     * @param file
     * @return
     */
    @Override
    public ResponseResult uploadImage(MultipartFile file) {
        //判断文件是否为空
        if (file == null) {
            return ResponseResult.FAILED("文件不可以为空");
        }
        // 判断文件类型 只支持 jpg gif png jpeg
        String contentType = file.getContentType();
        if (TextUtils.isEmpty(contentType)) {
            return ResponseResult.FAILED("图片格式错误");
        }
        // 获取相关数据 比如文件数据 文件名称
        String originalFilename = file.getOriginalFilename();
        log.info("originalFilename--->" + originalFilename); //真正的名字 有后缀
        String type = null;
        log.info("contentType-->" + contentType);
        switch (contentType) {
            case Constants.Image.TYPE_JPG_WITH_PREFIX:
            case Constants.Image.TYPE_JPEG_WITH_PREFIX:
                type = Constants.Image.TYPE_JPG;
                break;
            case Constants.Image.TYPE_PNG_WITH_PREFIX:
                type = Constants.Image.TYPE_PNG;
                break;
            case Constants.Image.TYPE_GIF_WITH_PREFIX:
                type = Constants.Image.TYPE_GIF;
                break;
            default:
                return ResponseResult.FAILED("图片格式不支持");
        }
        //限制文件大小
        long size = file.getSize();
        if (size > maxSize) {
            return ResponseResult.FAILED("图片超过最大尺寸");
        }
        // 根据我们的规则进行命名
        //创建对应的保存目夹 根目录/日期/图片类型/图片id.图片类型
        String currentDay = simpleDateFormat.format(new Date());
        //判断日期文件夹
        String dayPath = imagePath + File.separator + currentDay;
        File dayPathFile = new File(dayPath);
        if (!dayPathFile.exists()) {
            dayPathFile.mkdirs();
        }
        String imageId = String.valueOf(idWorker.nextId());
        String targetPath = dayPathFile + File.separator + type + File.separator + imageId + "." + type;
        File targetFile = new File(targetPath);
        //判断文件夹是否存在
        if (!targetFile.getParentFile().exists()) {
            targetFile.mkdirs();
        }
        log.info("targetFile==>" + targetFile);
        // 保存数据
        try {
            file.transferTo(targetFile);
            HashMap<String, String> data = new HashMap<>();
            String urlPath = (currentDay + File.separator + type + File.separator + imageId + "." + type)
                    .replace(File.separator, "-");
            data.put("path", urlPath);
            //保存数据
            Image image = new Image();
            image.setId(imageId);
            BlogUser blogUser = userService.checkBolgUser();
            image.setUserId(blogUser.getId());
            image.setUrl(urlPath);
            image.setPath(targetPath);
            image.setName(originalFilename);
            image.setState("1");
            image.setCreateTime(new Date());
            image.setUpdateTime(new Date());
            imageDao.save(image);
            return ResponseResult.SUCCESS("图片保存成功").setData(data);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.FAILED("图片保存失败");
        }
    }

    @Override
    public void getImage(String path, HttpServletResponse response) throws IOException {
        //todo 根据尺寸动态返回图片给前端

        // 优点：减少带宽，传输速率快
        // 缺点： CPU资源占用高
        // 也就是根据不同的使用，获取不同的图片尺寸，建议做法，将图片分为大、中、小三个尺寸
        // 按照需要进行获取
        File file = new File(imagePath + File.separator + path.replace("-", File.separator));
        String type = path.substring(path.length() - 3);
        response.setContentType("image/" + type);
        OutputStream writer = response.getOutputStream();
        FileInputStream fos = new FileInputStream(file);
        //读取
        byte[] buff = new byte[1024];
        int len;
        while ((len = fos.read(buff)) != -1) {
            writer.write(buff, 0, len);
        }
        writer.flush();
        if (writer != null) {
            writer.close();
        }
        if (fos != null) {
            fos.close();
        }
    }

    @Override
    public ResponseResult getImageList(int page, int size) {
        //检查数据
        page = checkPage(page);
        size = checkSize(size);
        //创建sort
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        //查询
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        //返回结果
        Page<Image> all = imageDao.findAll(new Specification<Image>() {
            @Override
            public Predicate toPredicate(Root<Image> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                //判断state是否为1
                return criteriaBuilder.equal(root.get("state").as(String.class), "1");
            }
        }, pageable);
        return ResponseResult.SUCCESS("查询结果成功").setData(all);
    }
}

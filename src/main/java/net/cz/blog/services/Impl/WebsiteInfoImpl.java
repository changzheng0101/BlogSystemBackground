package net.cz.blog.services.Impl;

import net.cz.blog.Dao.SettingsDao;
import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.Setting;
import net.cz.blog.services.IWebsiteInfoService;
import net.cz.blog.utils.Constants;
import net.cz.blog.utils.SnowflakeIdWorker;
import net.cz.blog.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class WebsiteInfoImpl extends BaseService implements IWebsiteInfoService {

    @Autowired
    private SettingsDao settingsDao;
    @Autowired
    private SnowflakeIdWorker idWorker;

    /**
     * 添加拦截器进行统计，要做细还得统计分来源
     *
     * @return
     */
    @Override
    public ResponseResult getWebsiteViewCount() {
        Setting viewCountFromDb = settingsDao.findOneByKey(Constants.Settings.WEBSITE_VIEW_COUNTS);
        if (viewCountFromDb == null) {
            viewCountFromDb = new Setting();
            viewCountFromDb.setId(idWorker.nextId() + "");
            viewCountFromDb.setKey(Constants.Settings.WEBSITE_KEYWORDS);
            viewCountFromDb.setValue("1");
            viewCountFromDb.setUpdateTime(new Date());
            viewCountFromDb.setCreateTime(new Date());
            settingsDao.save(viewCountFromDb);
        }
        Map<String, Integer> result = new HashMap<>();
        result.put(viewCountFromDb.getKey(), Integer.valueOf(viewCountFromDb.getValue()));
        return ResponseResult.SUCCESS("获取网站浏览量成功").setData(result);
    }

    @Override
    public ResponseResult putSeoInfo(String description, String keywords) {
        if (TextUtils.isEmpty(description)) {
            return ResponseResult.FAILED("描述不可以为空");
        }
        if (TextUtils.isEmpty(keywords)) {
            return ResponseResult.FAILED("关键字不可以为空");
        }
        Setting descriptionFromDb = settingsDao.findOneByKey(Constants.Settings.WEBSITE_DESCRIPTION);
        if (descriptionFromDb == null) {
            descriptionFromDb = new Setting();
            descriptionFromDb.setId(idWorker.nextId() + "");
            descriptionFromDb.setKey(Constants.Settings.WEBSITE_DESCRIPTION);
            descriptionFromDb.setUpdateTime(new Date());
            descriptionFromDb.setCreateTime(new Date());
        }
        descriptionFromDb.setValue(description);
        settingsDao.save(descriptionFromDb);
        Setting keywordsFromDb = settingsDao.findOneByKey(Constants.Settings.WEBSITE_KEYWORDS);
        if (keywordsFromDb == null) {
            keywordsFromDb = new Setting();
            keywordsFromDb.setId(idWorker.nextId() + "");
            keywordsFromDb.setKey(Constants.Settings.WEBSITE_KEYWORDS);
            keywordsFromDb.setUpdateTime(new Date());
            keywordsFromDb.setCreateTime(new Date());
        }
        descriptionFromDb.setValue(keywords);
        settingsDao.save(keywordsFromDb);
        return ResponseResult.SUCCESS("网站seo信息更新成功");
    }

    @Override
    public ResponseResult getSeoInfo() {
        Setting descriptionFromDb = settingsDao.findOneByKey(Constants.Settings.WEBSITE_DESCRIPTION);
        Setting keywordsFromDb = settingsDao.findOneByKey(Constants.Settings.WEBSITE_KEYWORDS);
        Map<String, String> result = new HashMap<>();
        result.put(descriptionFromDb.getKey(), descriptionFromDb.getValue());
        result.put(keywordsFromDb.getKey(), keywordsFromDb.getValue());
        return ResponseResult.SUCCESS("seo信息查询成功").setData(result);
    }

    @Override
    public ResponseResult updateWebsiteTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            return ResponseResult.FAILED("网站标题不可以为空");
        }
        Setting settingFromDb = settingsDao.findOneByKey(Constants.Settings.WEBSITE_TITLE);
        if (settingFromDb == null) {
            settingFromDb = new Setting();
            settingFromDb.setId(idWorker.nextId() + "");
            settingFromDb.setKey(Constants.Settings.WEBSITE_TITLE);
            settingFromDb.setUpdateTime(new Date());
            settingFromDb.setCreateTime(new Date());
        }
        settingFromDb.setValue(title);
        settingsDao.save(settingFromDb);
        return ResponseResult.SUCCESS("网站标题更新成功");
    }

    @Override
    public ResponseResult getWebsiteTitle() {
        Setting title = settingsDao.findOneByKey(Constants.Settings.WEBSITE_TITLE);
        return ResponseResult.SUCCESS("网站标题查询成功").setData(title);
    }
}

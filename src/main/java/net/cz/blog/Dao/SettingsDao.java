package net.cz.blog.Dao;

import net.cz.blog.pojo.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

//jpa内置了许多数据库操作 不再需要自己撰写
public interface SettingsDao extends JpaRepository<Setting, String>, JpaSpecificationExecutor<String> {
    Setting findValByKey(String key);
}

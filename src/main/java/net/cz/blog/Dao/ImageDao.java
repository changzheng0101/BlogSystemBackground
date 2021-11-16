package net.cz.blog.Dao;

import net.cz.blog.pojo.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ImageDao extends JpaRepository<Image, String>, JpaSpecificationExecutor<Image> {
}

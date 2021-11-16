package net.cz.blog.Dao;

import net.cz.blog.pojo.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ImageDao extends JpaRepository<Image, String>, JpaSpecificationExecutor<Image> {

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE `tb_images` SET `state`='0' WHERE `id`=?")
    int deleteImageByImageId(String imageId);


}

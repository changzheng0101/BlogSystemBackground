package net.cz.blog.Dao;

import net.cz.blog.pojo.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface LabelDao extends JpaRepository<Label, String>, JpaSpecificationExecutor<Label> {

    //这是可以自动生成
    @Modifying
    int deleteOneById(String id);

    //自己手动撰写
    @Modifying
    @Query(value = "DELETE FROM `tb_labels` WHERE id=?", nativeQuery = true)
    int customDelLabelById(String id);

    Label findOneById(String id);

    @Modifying
    @Query(nativeQuery = true, value = "update `tb_labels` set `count`=`count`+1 where `name`=?")
    int updateCountByName(String label);
}

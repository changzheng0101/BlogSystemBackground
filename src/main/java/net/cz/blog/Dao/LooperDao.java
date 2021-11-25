package net.cz.blog.Dao;

import net.cz.blog.pojo.Looper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LooperDao extends JpaRepository<Looper, String>, JpaSpecificationExecutor<Looper> {

    Looper findOneById(String looperId);

    @Query(nativeQuery = true, value = "select  * from `tb_looper` where `state`=?")
    List<Looper> listLooperByStatus(String state);
}

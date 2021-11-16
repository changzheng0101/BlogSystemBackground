package net.cz.blog.Dao;

import net.cz.blog.pojo.Looper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LooperDao extends JpaRepository<Looper, String>, JpaSpecificationExecutor<Looper> {

}

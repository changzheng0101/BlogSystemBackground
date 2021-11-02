package net.cz.blog;


import net.cz.blog.utils.SnowflakeIdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication()
public class BlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }

    @Bean
    public SnowflakeIdWorker createIdWorker() {
        return new SnowflakeIdWorker(0, 0);
    }

    @Bean
    public BCryptPasswordEncoder createEncoder(){
        return new BCryptPasswordEncoder();
    }
}

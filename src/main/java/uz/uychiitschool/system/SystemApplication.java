package uz.uychiitschool.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.domain.EntityScan;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
//@EnableJpaRepositories(basePackages = {
//        "uz.uychiitschool.system.web.base.repository",
//        "uz.uychiitschool.system.web.core.repository"
//})
//@EntityScan(basePackages = {
//        "uz.uychiitschool.system.web.base.entity",
//        "uz.uychiitschool.system.web.core.repository"
//})
public class SystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
    }

}

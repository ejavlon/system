package uz.uychiitschool.system.web.base.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import uz.uychiitschool.system.web.base.entity.User;

import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}

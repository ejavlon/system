package uz.uychiitschool.system.web.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uychiitschool.system.web.core.entity.Course;
import uz.uychiitschool.system.web.core.entity.Passport;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    boolean existsByName(String name);
}

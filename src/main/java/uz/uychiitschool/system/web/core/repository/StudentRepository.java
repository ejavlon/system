package uz.uychiitschool.system.web.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uychiitschool.system.web.core.entity.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
}

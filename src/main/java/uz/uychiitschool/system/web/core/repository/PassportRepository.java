package uz.uychiitschool.system.web.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uychiitschool.system.web.core.entity.Passport;

@Repository
public interface PassportRepository extends JpaRepository<Passport, Integer> {

    boolean existsBySerialAndNumber(String serial, String number);
}

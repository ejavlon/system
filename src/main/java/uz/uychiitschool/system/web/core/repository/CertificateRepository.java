package uz.uychiitschool.system.web.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uychiitschool.system.web.core.entity.Address;
import uz.uychiitschool.system.web.core.entity.Certificate;

import java.util.UUID;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, UUID> {

}

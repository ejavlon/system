package uz.uychiitschool.system.web.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uychiitschool.system.web.core.entity.Address;
import uz.uychiitschool.system.web.core.entity.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group,Integer> {
}

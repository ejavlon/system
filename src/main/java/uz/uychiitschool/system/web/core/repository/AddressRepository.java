package uz.uychiitschool.system.web.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uychiitschool.system.web.core.entity.Address;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
    boolean existsByRegionNameAndDistrictNameAndStreetNameAndHouseNumber(String regionName, String districtName, String streetName, Integer houseNumber);

    Optional<Address> findByRegionNameAndDistrictNameAndStreetNameAndHouseNumber(String regionName, String districtName, String streetName, Integer houseNumber);
}

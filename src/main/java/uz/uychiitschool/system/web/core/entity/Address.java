package uz.uychiitschool.system.web.core.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uychiitschool.system.web.base.entity.BaseEntity;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(
        name = "address",
        uniqueConstraints = @UniqueConstraint(columnNames = {"regionName", "districtName", "streetName", "houseNumber"})
)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address extends BaseEntity {
    @Column(length = 20)
    String regionName;

    @Column(length = 20)
    String districtName;

    @Column(length = 20)
    String streetName;

    Integer houseNumber;
}

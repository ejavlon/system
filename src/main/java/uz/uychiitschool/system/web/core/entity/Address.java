package uz.uychiitschool.system.web.core.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(
        name = "_address",
        uniqueConstraints = @UniqueConstraint(columnNames = {"regionName", "districtName", "streetName", "houseNumber"})
)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(length = 20)
    String regionName;

    @Column(length = 20)
    String districtName;

    @Column(length = 20)
    String streetName;

    Integer houseNumber;
}

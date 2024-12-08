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
        name = "_passport",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"serial", "number"})
        }
)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Passport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(length = 5)
    String serial;

    @Column(length = 20)
    String number;
}

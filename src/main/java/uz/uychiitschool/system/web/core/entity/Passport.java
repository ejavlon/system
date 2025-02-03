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
        name = "passport",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"serial", "number"})
        }
)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Passport extends BaseEntity {
    @Column(length = 5)
    String serial;

    @Column(length = 20)
    String number;
}

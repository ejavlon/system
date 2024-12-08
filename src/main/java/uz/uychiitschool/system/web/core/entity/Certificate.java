package uz.uychiitschool.system.web.core.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uychiitschool.system.web.base.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "_certificate")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(length = 10)
    String serial;

    @Column(length = 20)
    String number;

    LocalDateTime date;

    @OneToOne
    Course course;

    @OneToOne(cascade = CascadeType.ALL)
    Student student;

    @OneToOne
    User teacher;
}

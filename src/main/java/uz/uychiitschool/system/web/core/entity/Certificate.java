package uz.uychiitschool.system.web.core.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uychiitschool.system.web.base.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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

    @ManyToOne
    @JoinColumn(name = "course_id")  // Sertifikatlar ko'plab kurslarga bog'lanadi
    Course course;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "student_id")
    Student student;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    User teacher;
}

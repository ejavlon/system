package uz.uychiitschool.system.web.core.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uychiitschool.system.web.base.entity.User;
import uz.uychiitschool.system.web.core.enums.CourseType;
import uz.uychiitschool.system.web.core.enums.GroupStatus;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "_group")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(nullable = false)
    String name;

    Integer size;

    Integer currentSize;

    LocalDateTime startDate = LocalDateTime.now();

    LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    GroupStatus status;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    Course course;

    @OneToOne
    User teacher;

    @ManyToMany(mappedBy = "groups", fetch = FetchType.EAGER)
    private List<Student> students;

}

package uz.uychiitschool.system.web.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uychiitschool.system.web.base.entity.BaseEntity;
import uz.uychiitschool.system.web.base.entity.User;
import uz.uychiitschool.system.web.core.enums.GroupStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "_group")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Group extends BaseEntity {
    @Column(nullable = false, length = 50, unique = true)
    String name;

    Integer size;

    Integer currentSize;

    LocalDateTime startDate = LocalDateTime.now();

    LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    GroupStatus status;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    Course course;

    @OneToOne
    User teacher;

    @ManyToMany(mappedBy = "groups", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Student> students = new ArrayList<>();

}

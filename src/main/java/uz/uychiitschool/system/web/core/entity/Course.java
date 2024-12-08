package uz.uychiitschool.system.web.core.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "_passport")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(length = 50, unique = true, nullable = false)
    String name;

    @Column(columnDefinition = "DOUBLE DEFAULT 0.0")
    Double price;

    Integer duration;

    @OneToMany(mappedBy = "course",cascade = CascadeType.ALL)
    List<Group> groups = new ArrayList<>();
}

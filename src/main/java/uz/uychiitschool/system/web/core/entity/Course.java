package uz.uychiitschool.system.web.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uychiitschool.system.web.base.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "_course")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Course extends BaseEntity {
    @Column(length = 50, unique = true, nullable = false)
    String name;

    @Column(columnDefinition = "DOUBLE DEFAULT 0.0")
    Double price;

    Integer duration;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JsonIgnore
    List<Group> groups = new ArrayList<>();
}

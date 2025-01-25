package uz.uychiitschool.system.web.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uychiitschool.system.web.base.entity.BaseEntity;
import uz.uychiitschool.system.web.base.enums.Gender;

import java.time.LocalDate;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "_student")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Student extends BaseEntity {
    @OneToOne(cascade = {CascadeType.ALL})
    Address address;

    @OneToOne(cascade = {CascadeType.ALL})
    Passport passport;

    @Column(name = "first_name", length = 20, nullable = false)
    String firstName;

    @Column(name = "last_name", length = 20, nullable = false)
    String lastName;

    LocalDate birthday;

    @Column(name = "father_name", length = 30)
    String fatherName;

    @Enumerated(EnumType.STRING)
    Gender gender;

    @Column(length = 20)
    String phoneNumber;

    @Column(length = 20)
    String fatherPhoneNumber;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "student_group",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    @JsonIgnore
    List<Group> groups = new ArrayList<>();


//    @OneToMany(cascade = CascadeType.ALL)
//    List<Certificate> certificates;
}

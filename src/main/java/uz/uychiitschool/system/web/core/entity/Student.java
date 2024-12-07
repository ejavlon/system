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
@Table(name = "_student")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @OneToOne(cascade = {CascadeType.ALL})
    Address address;

    @Column(name = "first_name",length = 50)
    String firstName;

    @Column(name = "last_name",length = 50)
    String lastName;

    @Column(name = "father_name",length = 50)
    String fatherName;

    String phoneNumber;

    String fatherPhoneNumber;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "student_group",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    List<Group> groups = new ArrayList<>();

}

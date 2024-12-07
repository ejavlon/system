package uz.uychiitschool.system.web.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentDto {
    Integer id;

    String regionName;

    String districtName;

    String streetName;

    Integer houseNumber;

    String firstName;

    String lastName;

    String fatherName;

    String phoneNumber;

    String fatherPhoneNumber;
}

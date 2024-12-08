package uz.uychiitschool.system.web.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

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

    LocalDate birthDate;

    String gender;

    String passportSerial;

    String passportNumber;

    String fatherName;

    String phoneNumber;

    String fatherPhoneNumber;
}

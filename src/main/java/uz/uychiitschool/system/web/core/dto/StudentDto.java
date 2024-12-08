package uz.uychiitschool.system.web.core.dto;

import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "first name is null")
    String firstName;

    @NotNull(message = "last name is null")
    String lastName;

    LocalDate birthDate;

    @NotNull(message = "gender is null")
    String gender;

    String passportSerial;

    String passportNumber;

    String fatherName;

    String phoneNumber;

    String fatherPhoneNumber;
}

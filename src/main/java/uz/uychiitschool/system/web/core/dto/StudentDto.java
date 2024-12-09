package uz.uychiitschool.system.web.core.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
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

package uz.uychiitschool.system.web.core.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CertificateDto {
    UUID id;

    String serial;

    String number;

    LocalDateTime date;

    Boolean weekly;

    @NotNull(message = "course id is null")
    Integer courseId;

    @NotNull(message = "student id is null")
    Integer studentId;

    @NotNull(message = "teacher id is null")
    Integer teacherId;
}

package uz.uychiitschool.system.web.core.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupDto {
    Integer id;

    @NotNull(message = "name is null")
    String name;

    @NotNull(message = "group size is null")
    Integer size;

    Integer currentSize;

    LocalDateTime startDate;

    LocalDateTime endDate;

    @NotNull(message = "Group status is null")
    String status;

    @NotNull(message = "course id is null")
    Integer courseId;

    Integer teacherId;
}

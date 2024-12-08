package uz.uychiitschool.system.web.core.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

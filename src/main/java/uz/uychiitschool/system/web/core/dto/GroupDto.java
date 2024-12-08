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

    String name;

    Integer size;

    Integer currentSize;

    LocalDateTime startDate;

    LocalDateTime endDate;

    String status;

    Integer courseId;

    Integer teacherId;
}

package uz.uychiitschool.system.web.core.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseDto {
    Integer id;

    @NotNull(message = "name is null")
    String name;

    @NotNull(message = "price is null")
    Double price;

    @NotNull(message = "course duration month is null")
    Integer duration;
}

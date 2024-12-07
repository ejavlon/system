package uz.uychiitschool.system.web.base.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignInDto {
    @NotNull(message = "username null")
    String username;

    @NotNull(message = "password null")
    String password;
}

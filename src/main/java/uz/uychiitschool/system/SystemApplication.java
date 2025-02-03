package uz.uychiitschool.system;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import uz.uychiitschool.system.web.base.entity.User;
import uz.uychiitschool.system.web.base.enums.Gender;
import uz.uychiitschool.system.web.base.enums.Role;
import uz.uychiitschool.system.web.base.repository.UserRepository;
import uz.uychiitschool.system.web.base.service.UserService;

@SpringBootApplication
@RequiredArgsConstructor
public class SystemApplication {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(Environment environment){
        String initMode = environment.getProperty("spring.sql.init.mode");
        return args -> {
            if ("always".equals(initMode) && !userRepository.existsByUsername("ejavlon")) {
                var admin = User.builder()
                        .firstName("Javlon")
                        .lastName("Ergashev")
                        .username("ejavlon")
                        .password(passwordEncoder.encode("root"))
                        .role(Role.ADMIN)
                        .gender(Gender.MALE)
                        .build();
                userRepository.save(admin);
            }
        };
    }

}

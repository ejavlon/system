package uz.uychiitschool.system.web.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.uychiitschool.system.web.base.entity.User;
import uz.uychiitschool.system.web.base.enums.Gender;
import uz.uychiitschool.system.web.base.enums.Role;
import uz.uychiitschool.system.web.base.repository.UserRepository;
import uz.uychiitschool.system.web.core.entity.Course;
import uz.uychiitschool.system.web.core.repository.CourseRepository;

@Component
@RequiredArgsConstructor
public class CommandLineService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final CourseRepository courseRepository;

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

            if ("always".equals(initMode) && !userRepository.existsByUsername("rahmatillo")) {
                var teacher = User.builder()
                        .firstName("Rahmatillo")
                        .lastName("Muhammadshokirov")
                        .username("rahmatillo")
                        .password(passwordEncoder.encode("rahamtillo"))
                        .role(Role.TEACHER)
                        .gender(Gender.MALE)
                        .build();
                userRepository.save(teacher);
            }

            if ("always".equals(initMode) && !courseRepository.existsByName("Kompyuter savodxonligi")) {
                var course = Course.builder().name("Kompyuter savodxonligi").build();
                courseRepository.save(course);
            }
        };
    }
}

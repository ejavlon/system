package uz.uychiitschool.system.web.base.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import uz.uychiitschool.system.web.base.dto.ResponseApi;
import uz.uychiitschool.system.web.base.dto.SignInDto;
import uz.uychiitschool.system.web.base.dto.SignUpDto;
import uz.uychiitschool.system.web.base.entity.User;
import uz.uychiitschool.system.web.base.enums.Role;
import uz.uychiitschool.system.web.base.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService extends BaseService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    public ResponseApi<String> signIn(SignInDto signInDto){
        try {
            Optional<User> optionalUser = userRepository.findByUsername(signInDto.getUsername());
            if (optionalUser.isEmpty())
                return ResponseApi.<String>builder()
                        .success(false)
                        .message("No such user")
                        .build();

            User user = optionalUser.get();
            if (!passwordEncoder.matches(signInDto.getPassword(), user.getPassword()))
                return ResponseApi.<String>builder()
                        .success(false)
                        .message("No such user")
                        .build();

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            signInDto.getUsername(),
                            signInDto.getPassword()
                    )
            );

            return ResponseApi.<String>builder()
                    .success(true)
                    .message("Welcome to system")
                    .data(jwtService.generateToken(user))
                    .build();
        }catch (RuntimeException e){
            return errorMessage();
        }
    }

    public ResponseApi<User> signUp(SignUpDto userDto) {
        try {
            Optional<User> optionalUser = userRepository.findByUsername(userDto.getUsername());
            if (optionalUser.isPresent())
                return ResponseApi.<User>builder()
                        .success(true)
                        .message("such username is registered in the system")
                        .build();

            var user = User.builder()
                    .firstName(userDto.getFirstName())
                    .lastName(userDto.getLastName())
                    .username(userDto.getUsername())
                    .password(passwordEncoder.encode(userDto.getPassword()))
                    .role(Role.USER)
                    .build();
            user = userRepository.save(user);

            return ResponseApi.<User>builder()
                    .success(true)
                    .message("Successfully created")
                    .data(user)
                    .build();
        }catch (RuntimeException e){
            return errorMessage();
        }
    }

    public ResponseApi<List<User>> getAllUsers() {
        try {
            return ResponseApi.<List<User>>builder()
                    .success(true)
                    .message("All users")
                    .data(userRepository.findAll())
                    .build();
        }catch (RuntimeException e){
            return errorMessage();
        }
    }

    public ResponseApi<User> editUserById(Integer id,SignUpDto user) {
        try {
            Optional<User> optionalUser = userRepository.findById(id);
            if (optionalUser.isEmpty())
                return ResponseApi.<User>builder()
                        .success(false)
                        .message("User not found")
                        .build();

            User editedUser = optionalUser.get();

            Optional<User> optionalUserByUsername = userRepository.findByUsername(user.getUsername());
            if (optionalUserByUsername.isPresent()){
                User user1 = optionalUserByUsername.get();
                if (!Objects.equals(user1.getId(), editedUser.getId()))
                    return ResponseApi.<User>builder()
                            .success(true)
                            .message("such username is registered in the system")
                            .build();
            }

            editedUser.setFirstName(user.getFirstName());
            editedUser.setLastName(user.getLastName());
            editedUser.setPassword(user.getPassword());
            editedUser.setUsername(user.getUsername());
            editedUser = userRepository.save(editedUser);

            return ResponseApi.<User>builder()
                    .success(true)
                    .message("Successfully updated")
                    .data(editedUser)
                    .build();
        }catch (RuntimeException e){
            return errorMessage();
        }
    }

    public ResponseApi<User> deleteUserById(Integer id){
        try {
            Optional<User> optionalUser = userRepository.findById(id);
            if (optionalUser.isEmpty())
                return ResponseApi.<User>builder()
                        .success(false)
                        .message("User not found")
                        .build();

            userRepository.deleteById(id);

            return ResponseApi.<User>builder()
                    .success(true)
                    .message("Successfully deleted")
                    .build();
        }catch (RuntimeException e){
            return errorMessage();
        }
    }
}

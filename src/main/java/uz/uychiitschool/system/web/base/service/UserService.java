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
import uz.uychiitschool.system.web.base.exception.DataNotFoundException;
import uz.uychiitschool.system.web.base.exception.PasswordMismatchException;
import uz.uychiitschool.system.web.base.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    public ResponseApi<String> signIn(SignInDto signInDto){
        User user = userRepository.findByUsername(signInDto.getUsername()).orElseThrow(() -> new DataNotFoundException("User not found"));

        if (!passwordEncoder.matches(signInDto.getPassword(), user.getPassword()))
            throw new PasswordMismatchException("Password mismatch");

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
    }

    public ResponseApi<User> signUp(SignUpDto userDto) {
        Optional<User> optionalUser = userRepository.findByUsername(userDto.getUsername());
        if (optionalUser.isPresent())
            throw new DataNotFoundException("Username is already taken");

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
    }

    public ResponseApi<List<User>> getAllUsers() {
        return ResponseApi.<List<User>>builder()
                .success(true)
                .message("All users")
                .data(userRepository.findAll())
                .build();
    }

    public ResponseApi<User> editUserById(Integer id,SignUpDto user) {
        User editedUser = userRepository.findById(id).orElseThrow(() -> new DataNotFoundException("User not found"));

        Optional<User> optionalUserByUsername = userRepository.findByUsername(user.getUsername());
        if (optionalUserByUsername.isPresent()){
            User user1 = optionalUserByUsername.get();
            if (!Objects.equals(user1.getId(), editedUser.getId()))
                throw new DataNotFoundException("Username is already taken");
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
    }

    public ResponseApi<User> deleteUserById(Integer id){
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
    }
}

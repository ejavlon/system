package uz.uychiitschool.system.web.base.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.uychiitschool.system.web.base.dto.ResponseApi;
import uz.uychiitschool.system.web.base.dto.SignInDto;
import uz.uychiitschool.system.web.base.dto.SignUpDto;
import uz.uychiitschool.system.web.base.entity.User;
import uz.uychiitschool.system.web.base.enums.Gender;
import uz.uychiitschool.system.web.base.enums.Role;
import uz.uychiitschool.system.web.base.exception.DataNotFoundException;
import uz.uychiitschool.system.web.base.exception.DuplicateEntityException;
import uz.uychiitschool.system.web.base.exception.PasswordMismatchException;
import uz.uychiitschool.system.web.base.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    public ResponseApi<String> signIn(SignInDto signInDto){
        User user = findUserByIdOrUsernameOrThrow(null, signInDto.getUsername());

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
        if (userRepository.existsByUsername(userDto.getUsername())){
            throw new RuntimeException("Username exist");
        }
        var user = createOrUpdateUser(userDto, null);
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
        User editedUser = findUserByIdOrUsernameOrThrow(id,null);

        editedUser = createOrUpdateUser(user, editedUser);
        editedUser = userRepository.save(editedUser);

        return ResponseApi.<User>builder()
                .success(true)
                .message("Successfully updated")
                .data(editedUser)
                .build();
    }

    public ResponseApi<User> deleteUserById(Integer id){
        findUserByIdOrUsernameOrThrow(id, null);
        userRepository.deleteById(id);

        return ResponseApi.<User>builder()
                .success(true)
                .message("Successfully deleted")
                .build();
    }

    public User findUserByIdOrUsernameOrThrow(Integer id, String username){
        if (username == null)
            return userRepository.findById(id).orElseThrow(() -> new DataNotFoundException("User not found"));

        return userRepository.findByUsername(username).orElseThrow(() -> new DataNotFoundException("User not found"));
    }

    public User createUser(SignUpDto userDto) {
        Gender gender = Gender.fromString(userDto.getGender().toUpperCase());
        return User.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .gender(gender)
                .role(Role.USER)
                .build();
    }

    public User createOrUpdateUser(SignUpDto userDto, User existingUser) {
        if (existingUser == null){
            return createUser(userDto);
        }

        if (existingUser.getUsername().equals(userDto.getUsername()))
            userRepository.findByUsername(userDto.getUsername()).orElseThrow(() -> new DuplicateEntityException("Username already exists"));

        if (Objects.nonNull(existingUser.getFirstName()))
            existingUser.setFirstName(userDto.getFirstName());

        if (Objects.nonNull(userDto.getLastName()))
            existingUser.setLastName(userDto.getLastName());

        if (Objects.nonNull(userDto.getPassword()))
            existingUser.setPassword(userDto.getPassword());

        if (Objects.nonNull(userDto.getUsername()))
            existingUser.setUsername(userDto.getUsername());

        if (Objects.nonNull(userDto.getGender()))
            existingUser.setGender(Gender.fromString(userDto.getGender()));

        return existingUser;
    }
}

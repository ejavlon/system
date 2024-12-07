package uz.uychiitschool.system.web.base.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.uychiitschool.system.web.base.dto.ResponseApi;
import uz.uychiitschool.system.web.base.dto.SignInDto;
import uz.uychiitschool.system.web.base.dto.SignUpDto;
import uz.uychiitschool.system.web.base.entity.User;
import uz.uychiitschool.system.web.base.service.UserService;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {

    private final UserService userService;

    @PostMapping("/auth/signin")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInDto signInDto){
        ResponseApi<String> responseApi = userService.signIn(signInDto);
        return ResponseEntity.status(responseApi.isSuccess() ? OK : FORBIDDEN).body(responseApi);
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpDto userDto){
        ResponseApi<User> responseApi = userService.signUp(userDto);
        return ResponseEntity.status(responseApi.isSuccess() ? CREATED : CONFLICT).body(responseApi);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            description = "Get endpoint for admin",
            summary = "Login as admin to access this endpoint",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid token",
                            responseCode = "403"
                    )
            }
    )
    public ResponseEntity<?> getAllUsers(){
        ResponseApi<List<User>> responseApi = userService.getAllUsers();
        return ResponseEntity.ok(responseApi);
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            description = "Put endpoint for admin",
            summary = "Login as admin to access this endpoint",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid token",
                            responseCode = "403"
                    ),
                    @ApiResponse(
                            description = "There is no such endpoint",
                            responseCode = "404"

                    )
            }
    )
    public ResponseEntity<?> editUserById(@PathVariable Integer id, @Valid SignUpDto user){
        ResponseApi<User> responseApi = userService.editUserById(id,user);
        return ResponseEntity.status(responseApi.isSuccess() ? OK : NOT_FOUND).body(responseApi);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    @Operation(
            description = "Delete endpoint for admin",
            summary = "Login as admin to access this endpoint",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "204"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid token",
                            responseCode = "403"
                    ),
                    @ApiResponse(
                            description = "There is no such endpoint",
                            responseCode = "404"
                    )
            }
    )
    public ResponseEntity<?> deleteUserById(@PathVariable Integer id){
        ResponseApi<User> responseApi = userService.deleteUserById(id);
        return ResponseEntity.status(responseApi.isSuccess() ? NO_CONTENT: NOT_FOUND).body(responseApi);
    }

}

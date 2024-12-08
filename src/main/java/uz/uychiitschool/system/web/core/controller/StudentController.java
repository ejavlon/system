package uz.uychiitschool.system.web.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uychiitschool.system.web.base.dto.ResponseApi;
import uz.uychiitschool.system.web.core.dto.StudentDto;
import uz.uychiitschool.system.web.core.entity.Student;
import uz.uychiitschool.system.web.core.service.StudentService;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService service;

    @GetMapping
    public ResponseEntity<?> getAllStudents(@RequestParam(required = false) Integer page,
                                            @RequestParam(required = false) Integer size){
        ResponseApi<Page<Student>> responseApi = service.getAllStudents(page != null ? page : 0, size != null ? size : 10);
        return ResponseEntity.status(responseApi.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(responseApi);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable int id){
        ResponseApi<Student> responseApi = service.getStudentById(id);
        return ResponseEntity.status(responseApi.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(responseApi);
    }

    @PostMapping
    public ResponseEntity<?> createStudent(@Valid @RequestBody StudentDto studentDto){
        ResponseApi<Student> responseApi = service.createStudent(studentDto);
        return ResponseEntity.status(responseApi.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST).body(responseApi);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable int id, @Valid @RequestBody StudentDto studentDto){
        ResponseApi<Student> responseApi = service.updateStudentById(id,studentDto);
        return ResponseEntity.status(responseApi.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(responseApi);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudentById(@PathVariable int id){
        ResponseApi<Student> responseApi = service.deleteStudentById(id);
        return ResponseEntity.status(responseApi.isSuccess() ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND).body(responseApi);
    }
}

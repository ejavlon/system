package uz.uychiitschool.system.web.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
        return ResponseEntity.status(responseApi.isSuccess() ? 200 : 400).body(responseApi);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable int id){
        ResponseApi<Student> responseApi = service.getStudentById(id);
        return ResponseEntity.status(responseApi.isSuccess() ? 200 : 400).body(responseApi);
    }

    @PostMapping
    public ResponseEntity<?> createStudent(@RequestBody StudentDto studentDto){
        ResponseApi<Student> responseApi = service.createStudent(studentDto);
        return ResponseEntity.status(responseApi.isSuccess() ? 201 : 400).body(responseApi);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable int id, @RequestBody StudentDto studentDto){
        ResponseApi<Student> responseApi = service.updateStudentById(id,studentDto);
        return ResponseEntity.status(responseApi.isSuccess() ? 200 : 404).body(responseApi);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudentById(@PathVariable int id){
        ResponseApi<Student> responseApi = service.deleteStudentById(id);
        return ResponseEntity.status(responseApi.isSuccess() ? 204 : 404).body(responseApi);
    }
}

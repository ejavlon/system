package uz.uychiitschool.system.web.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uychiitschool.system.web.base.dto.ResponseApi;
import uz.uychiitschool.system.web.core.entity.Course;
import uz.uychiitschool.system.web.core.service.CourseService;

@RestController
@RequestMapping("/api/v1/course")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService service;

    @GetMapping
    public ResponseEntity<?> getAllCourses(@RequestParam(required = false) Integer page,
                                           @RequestParam(required = false) Integer size) {
        ResponseApi<Page<Course>> responseApi = service.getAllCourses(page == null ? 1 : page, size == null ? 10 : size);
        return ResponseEntity.ok(responseApi);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Integer id) {
        ResponseApi<Course> responseApi = service.getCourseById(id);
        return ResponseEntity.status(responseApi.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(responseApi);
    }

    @PostMapping
    public ResponseEntity<?> createCourse(@RequestBody Course course) {
        ResponseApi<Course> responseApi = service.createCourse(course);
        return ResponseEntity.status(responseApi.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST).body(responseApi);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable Integer id, @RequestBody Course course) {
        ResponseApi<Course> responseApi = service.updateCourse(id, course);
        return ResponseEntity.status(responseApi.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(responseApi);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Integer id) {
        ResponseApi<Course> responseApi = service.deleteCourse(id);
        return ResponseEntity.status(responseApi.isSuccess() ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND).body(responseApi);
    }
}

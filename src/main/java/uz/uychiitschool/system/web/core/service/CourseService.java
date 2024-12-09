package uz.uychiitschool.system.web.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uz.uychiitschool.system.web.base.dto.ResponseApi;
import uz.uychiitschool.system.web.base.exception.DataNotFoundException;
import uz.uychiitschool.system.web.base.exception.DuplicateEntityException;
import uz.uychiitschool.system.web.core.dto.CourseDto;
import uz.uychiitschool.system.web.core.entity.Course;
import uz.uychiitschool.system.web.core.mapper.CourseMapper;
import uz.uychiitschool.system.web.core.repository.CourseRepository;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository repository;
    private final CourseMapper courseMapper;

    public ResponseApi<Page<Course>> getAllCourses(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.sort(Course.class)
                .by(Course::getName).ascending());

        Page<Course> courses = repository.findAll(pageable);
        return ResponseApi.<Page<Course>>builder()
                .message(String.format("Courses from %s to %s", page * size, page * size + size))
                .data(courses)
                .success(true)
                .build();
    }

    public ResponseApi<Course> getCourseById(Integer id) {
        Course course = repository.findById(id).orElseThrow(() -> new DataNotFoundException("Course not found"));
        return ResponseApi.<Course>builder()
                .message("Course found")
                .data(course)
                .success(true)
                .build();
    }

    public ResponseApi<Course> createCourse(CourseDto courseDto) {
        if (repository.existsByName(courseDto.getName()))
            throw new DuplicateEntityException("Course already exists");

        Course course = Course.builder()
                .name(courseDto.getName())
                .duration(courseDto.getDuration())
                .price(courseDto.getPrice())
                .build();

        course = repository.save(course);
        repository.flush();

        return ResponseApi.<Course>builder()
                .success(true)
                .data(course)
                .message("Course successfully created")
                .build();
    }

    public ResponseApi<Course> updateCourse(Integer id, CourseDto newCourse) {
        Course course = repository.findById(id).orElseThrow(() -> new DataNotFoundException("Course not found"));
        courseMapper.updateCourseFromDto(newCourse, course);
        course = repository.save(course);
        repository.flush();

        return ResponseApi.<Course>builder()
                .success(true)
                .data(course)
                .message("Course successfully updated")
                .build();
    }

    public ResponseApi<Course> deleteCourse(Integer id) {
        Course course = repository.findById(id).orElseThrow(() -> new DataNotFoundException("Course not found"));
        repository.deleteById(id);
        repository.flush();

        return ResponseApi.<Course>builder()
                .success(true)
                .data(course)
                .message("Course successfully deleted")
                .build();
    }
}
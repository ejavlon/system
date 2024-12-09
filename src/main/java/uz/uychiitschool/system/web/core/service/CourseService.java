package uz.uychiitschool.system.web.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uz.uychiitschool.system.web.base.dto.ResponseApi;
import uz.uychiitschool.system.web.base.service.BaseService;
import uz.uychiitschool.system.web.core.dto.CourseDto;
import uz.uychiitschool.system.web.core.entity.Course;
import uz.uychiitschool.system.web.core.repository.CourseRepository;

@Service
@RequiredArgsConstructor
public class CourseService extends BaseService {
    private final CourseRepository repository;

    public ResponseApi<Page<Course>> getAllCourses(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.sort(Course.class)
                    .by(Course::getName).ascending());

            Page<Course> courses = repository.findAll(pageable);
            return ResponseApi.<Page<Course>>builder()
                    .message(String.format("Courses from %s to %s", page * size, page * size + size))
                    .data(courses)
                    .success(true)
                    .build();
        } catch (RuntimeException e) {
            return errorMessage(e.getMessage());
        }
    }

    public ResponseApi<Course> getCourseById(Integer id) {
        try {
            Course course = repository.findById(id).orElseThrow(() -> new RuntimeException("Course not found"));
            return ResponseApi.<Course>builder()
                    .message("Course found")
                    .data(course)
                    .success(true)
                    .build();
        } catch (RuntimeException e) {
            return errorMessage(e.getMessage());
        }
    }

    public ResponseApi<Course> createCourse(CourseDto courseDto) {
        try {
            if (repository.existsByName(courseDto.getName()))
                return ResponseApi.<Course>builder()
                        .success(false)
                        .message("Course exists")
                        .build();

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
        } catch (RuntimeException e) {
            return errorMessage(e.getMessage());
        }
    }

    public ResponseApi<Course> updateCourse(Integer id, CourseDto newCourse) {
        try {
            Course course = repository.findById(id).orElseThrow(() -> new RuntimeException("Course not found"));
            course.setName(newCourse.getName());
            course.setDuration(newCourse.getDuration());
            course.setPrice(newCourse.getPrice());
            course = repository.save(course);
            repository.flush();

            return ResponseApi.<Course>builder()
                    .success(true)
                    .data(course)
                    .message("Course successfully updated")
                    .build();

        } catch (RuntimeException e) {
            return errorMessage(e.getMessage());
        }
    }

    public ResponseApi<Course> deleteCourse(Integer id) {
        try {
            Course course = repository.findById(id).orElseThrow(() -> new RuntimeException("Course not found"));
            repository.deleteById(id);
            repository.flush();

            return ResponseApi.<Course>builder()
                    .success(true)
                    .data(course)
                    .message("Course successfully deleted")
                    .build();
        } catch (RuntimeException e) {
            return errorMessage(e.getMessage());
        }
    }

}

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
import uz.uychiitschool.system.web.core.repository.CourseRepository;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository repository;

    public ResponseApi<Page<Course>> getAllCourses(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.sort(Course.class)
                .by(Course::getName).ascending());

        Page<Course> courses = repository.findAll(pageable);
        return ResponseApi.createResponse(
                courses,
                String.format("Courses from %s to %s", page * size, Math.min((int) courses.getTotalElements(), page * size + size)),
                true
        );
    }

    public ResponseApi<Course> getCourseById(Integer id) {
        Course course = findCourseByIdOrThrow(id);
        return ResponseApi.createResponse(course, "Course found", true);
    }

    public ResponseApi<Course> create(CourseDto courseDto) {
        if (repository.existsByName(courseDto.getName()))
            throw new DuplicateEntityException("Course already exists");

        Course course = createOrUpdateCourse(courseDto, null);
        return ResponseApi.createResponse(course, "Course successfully created", true);
    }

    public ResponseApi<Course> updateCourse(Integer id, CourseDto newCourse) {
        Course course = findCourseByIdOrThrow(id);
        course = createOrUpdateCourse(newCourse, course);
        return ResponseApi.createResponse(course, "Course successfully updated", true);
    }

    public ResponseApi<Course> deleteCourse(Integer id) {
        Course course = findCourseByIdOrThrow(id);
        repository.deleteById(id);
        repository.flush();
        return ResponseApi.createResponse(course, "Course successfully deleted", true);
    }

    public Course findCourseByIdOrThrow(Integer id) {
        return repository.findById(id).orElseThrow(() -> new DataNotFoundException("Course not found"));
    }

    public Course createCourse(CourseDto courseDto) {
        return Course.builder()
                .name(courseDto.getName())
                .duration(courseDto.getDuration())
                .price(courseDto.getPrice())
                .build();
    }

    public Course createOrUpdateCourse(CourseDto courseDto, Course existingCourse) {
        if (existingCourse == null)
            return repository.save(createCourse(courseDto));

        if (courseDto.getName() != null)
            if (repository.existsByName(courseDto.getName()))
                throw new DuplicateEntityException("Course already exists");
            else
                existingCourse.setName(courseDto.getName());

        if (courseDto.getDuration() != null)
            existingCourse.setDuration(courseDto.getDuration());

        if (courseDto.getPrice() != null)
            existingCourse.setPrice(courseDto.getPrice());

        return repository.save(existingCourse);
    }
}
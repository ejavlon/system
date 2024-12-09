package uz.uychiitschool.system.web.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import uz.uychiitschool.system.web.core.dto.CourseDto;
import uz.uychiitschool.system.web.core.entity.Course;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CourseMapper {

    // DTO obyektni Entity obyektga o'tkazish
    @Mapping(target = "id", ignore = true)
    void updateCourseFromDto(CourseDto courseDto, @MappingTarget Course course);

    // Entity obyektni DTO obyektga aylantirish (optional)
    CourseDto toDto(Course course);
}

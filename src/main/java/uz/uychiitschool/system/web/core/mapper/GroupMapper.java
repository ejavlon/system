package uz.uychiitschool.system.web.core.mapper;

import org.mapstruct.*;
import uz.uychiitschool.system.web.core.dto.GroupDto;
import uz.uychiitschool.system.web.core.entity.Group;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GroupMapper {

    // DTO obyektni Entity obyektga o'tkazish
    void updateGroupFromDto(GroupDto groupDto, @MappingTarget Group group);

    // Entity obyektni DTO obyektga aylantirish (optional)
    GroupDto toDto(Group group);
}

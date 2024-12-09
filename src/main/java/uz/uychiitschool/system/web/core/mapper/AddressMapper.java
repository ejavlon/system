package uz.uychiitschool.system.web.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import uz.uychiitschool.system.web.core.entity.Address;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AddressMapper {

    @Mapping(target = "id", ignore = true)
    void updateAddressFromDto(Address addressDto, @MappingTarget Address address);

}

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
import uz.uychiitschool.system.web.core.dto.StudentDto;
import uz.uychiitschool.system.web.core.entity.Address;
import uz.uychiitschool.system.web.core.mapper.AddressMapper;
import uz.uychiitschool.system.web.core.repository.AddressRepository;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository repository;
    private final AddressMapper addressMapper;

    public ResponseApi<Page<Address>> getAllAddresses(int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.sort(Address.class).
                        by(Address::getRegionName).ascending()
                        .and(Sort.sort(Address.class).by(Address::getDistrictName).ascending()
                                .and(Sort.sort(Address.class).by(Address::getHouseNumber).ascending())));

        Page<Address> addresses = repository.findAll(pageable);
        return ResponseApi.<Page<Address>>builder()
                .data(addresses)
                .success(true)
                .message(String.format("Address from %s to %s", page * size, page * size + size))
                .build();
    }

    public ResponseApi<Address> getAddressById(int id) {
        Address address = repository.findById(id).orElseThrow(() -> new DataNotFoundException("Address not found"));
        return ResponseApi.<Address>builder()
                .data(address)
                .success(true)
                .message("Address found")
                .build();
    }

    public ResponseApi<Address> create(Address address) {
        if (repository.existsByRegionNameAndDistrictNameAndStreetNameAndHouseNumber(
                address.getRegionName(),
                address.getDistrictName(),
                address.getStreetName(),
                address.getHouseNumber())){
            throw new DuplicateEntityException("Duplicate address");
        }


        return ResponseApi.<Address>builder()
                .message("Address successfully saved")
                .data(repository.save(address))
                .success(true)
                .build();
    }

    public ResponseApi<Address> update(Address newAddress, int id) {
        Address oldAddress = repository.findById(id).orElseThrow(() -> new DataNotFoundException("Address not found"));
        addressMapper.updateAddressFromDto(newAddress, oldAddress);
        oldAddress = repository.save(oldAddress);

        return ResponseApi.<Address>builder()
                .success(true)
                .message("Address successfully updated")
                .data(oldAddress)
                .build();
    }

    public ResponseApi<Address> delete(int id) {
        Address address = repository.findById(id).orElseThrow(() -> new DataNotFoundException("Address not found"));
        repository.delete(address);
        return ResponseApi.<Address>builder()
                .success(true)
                .message("Address successfully deleted")
                .data(address)
                .build();
    }

    public Address createAddress(StudentDto studentDto) {
        return Address.builder()
                .regionName(studentDto.getRegionName())
                .districtName(studentDto.getDistrictName())
                .streetName(studentDto.getStreetName())
                .houseNumber(studentDto.getHouseNumber())
                .build();
    }

    public Address updateOrCreateAddress(StudentDto studentDto, Address existingAddress) {
        if (existingAddress == null) {
            return createAddress(studentDto);
        }
        addressMapper.updateAddressFromDto(createAddress(studentDto), existingAddress);
        return existingAddress;
    }

    public Address findByFullField(Address address){
        return repository.findByRegionNameAndDistrictNameAndStreetNameAndHouseNumber(
                address.getRegionName(),
                address.getDistrictName(),
                address.getStreetName(),
                address.getHouseNumber()
        ).orElse(null);
    }

}

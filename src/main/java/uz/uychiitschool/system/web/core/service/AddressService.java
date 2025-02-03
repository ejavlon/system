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
import uz.uychiitschool.system.web.core.repository.AddressRepository;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository repository;

    public ResponseApi<Page<Address>> getAllAddresses(int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.sort(Address.class).
                        by(Address::getRegionName).ascending()
                        .and(Sort.sort(Address.class).by(Address::getDistrictName).ascending()
                                .and(Sort.sort(Address.class).by(Address::getHouseNumber).ascending())));

        Page<Address> addresses = repository.findAll(pageable);
        return ResponseApi.createResponse(
                addresses,
                String.format("Address from %s to %s", page * size, Math.min((int) addresses.getTotalElements(), page * size + size)),
                true
        );
    }

    public ResponseApi<Address> getAddressById(int id) {
        Address address = findAddressById(id);
        return ResponseApi.createResponse(address, "Address found", true);
    }

    public ResponseApi<Address> create(Address address) {
        if (repository.existsByRegionNameAndDistrictNameAndStreetNameAndHouseNumber(
                address.getRegionName(),
                address.getDistrictName(),
                address.getStreetName(),
                address.getHouseNumber())) {
            throw new DuplicateEntityException("Address already exists");
        }
        address = updateOrCreateAddress(address, null);
        return ResponseApi.createResponse(address, "Address successfully saved", true);
    }

    public ResponseApi<Address> update(Address newAddress, int id) {
        Address oldAddress = findAddressById(id);
        oldAddress = updateOrCreateAddress(newAddress, oldAddress);
        return ResponseApi.createResponse(oldAddress, "Address successfully updated", true);
    }

    public ResponseApi<Address> delete(int id) {
        Address address = findAddressById(id);
        repository.delete(address);
        return ResponseApi.createResponse(address, "Address successfully deleted", true);
    }

    public Address createAddress(StudentDto studentDto) {
        return Address.builder()
                .regionName(studentDto.getRegionName())
                .districtName(studentDto.getDistrictName())
                .streetName(studentDto.getStreetName())
                .houseNumber(studentDto.getHouseNumber())
                .build();
    }

    public Address updateOrCreateAddress(Address address, Address existingAddress) {
        if (existingAddress == null)
            return repository.save(address);

        if (address.getRegionName() != null)
            existingAddress.setRegionName(address.getRegionName());

        if (address.getDistrictName() != null)
            existingAddress.setDistrictName(address.getDistrictName());

        if (address.getStreetName() != null)
            existingAddress.setStreetName(address.getStreetName());

        if (address.getHouseNumber() != null)
            existingAddress.setHouseNumber(address.getHouseNumber());

        return repository.save(existingAddress);
    }

    public Address updateOrCreateAddress(StudentDto studentDto, Address existingAddress) {
        if (existingAddress == null) {
            return createAddress(studentDto);
        }
        if (studentDto.getRegionName() == null && studentDto.getDistrictName() == null
            && studentDto.getStreetName() == null && studentDto.getHouseNumber() == null) {
            return null;
        }
        Address newAddress = createAddress(studentDto);
        existingAddress = updateOrCreateAddress(newAddress, existingAddress);
        return existingAddress;
    }

    public Address findAddressById(int id) {
        return repository.findById(id).orElseThrow(() -> new DataNotFoundException("Address not found"));
    }
}

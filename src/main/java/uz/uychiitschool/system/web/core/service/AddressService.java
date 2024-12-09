package uz.uychiitschool.system.web.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uz.uychiitschool.system.web.base.dto.ResponseApi;
import uz.uychiitschool.system.web.base.service.BaseService;
import uz.uychiitschool.system.web.core.entity.Address;
import uz.uychiitschool.system.web.core.exception.DataNotFoundException;
import uz.uychiitschool.system.web.core.mapper.AddressMapper;
import uz.uychiitschool.system.web.core.repository.AddressRepository;

@Service
@RequiredArgsConstructor
public class AddressService extends BaseService {

    private final AddressRepository repository;
    private final AddressMapper addressMapper;

    public ResponseApi<Page<Address>> getAllAddresses(int page, int size) {
        try {
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
        } catch (RuntimeException e) {
            return errorMessage(e.getMessage());
        }

    }

    public ResponseApi<Address> getAddressById(int id) {
        try {
            Address address = repository.findById(id).orElseThrow(() -> new DataNotFoundException("Address not found"));
            return ResponseApi.<Address>builder()
                    .data(address)
                    .success(true)
                    .message("Address found")
                    .build();
        } catch (DataNotFoundException e) {
            return errorMessage(e.getMessage());
        }
    }

    public ResponseApi<Address> create(Address address) {
        try {
            if (repository.existsByRegionNameAndDistrictNameAndStreetNameAndHouseNumber(
                    address.getRegionName(),
                    address.getDistrictName(),
                    address.getStreetName(),
                    address.getHouseNumber())) {

                return ResponseApi.<Address>builder()
                        .message("Address exists")
                        .success(false)
                        .build();
            }

            return ResponseApi.<Address>builder()
                    .message("Address successfully saved")
                    .data(repository.save(address))
                    .success(true)
                    .build();
        } catch (RuntimeException e) {
            return errorMessage(e.getMessage());
        }
    }

    public ResponseApi<Address> update(Address newAddress, int id) {
        try {
            Address oldAddress = repository.findById(id).orElseThrow(() -> new DataNotFoundException("Address not found"));
            oldAddress = updateAddressNotNullField(newAddress, oldAddress);
            oldAddress = repository.save(oldAddress);

            return ResponseApi.<Address>builder()
                    .success(true)
                    .message("Address successfully updated")
                    .data(oldAddress)
                    .build();
        } catch (DataNotFoundException e) {
            return errorMessage(e.getMessage());
        }
    }

    public Address updateAddressNotNullField(Address newAddress, Address oldAddress) {
        addressMapper.updateAddressFromDto(newAddress, oldAddress);
        return oldAddress;
    }

    public ResponseApi<Address> delete(int id) {
        try {
            Address address = repository.findById(id).orElseThrow(() -> new DataNotFoundException("Address not found"));
            repository.delete(address);
            return ResponseApi.<Address>builder()
                    .success(true)
                    .message("Address successfully deleted")
                    .data(address)
                    .build();
        } catch (DataNotFoundException e) {
            return errorMessage(e.getMessage());
        }
    }

}

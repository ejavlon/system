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
import uz.uychiitschool.system.web.core.repository.AddressRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddressService extends BaseService {

    private final AddressRepository repository;

    public ResponseApi<Page<Address>> getAllAddresses(int page, int size){
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
                    .message("All addresses")
                    .build();
        }catch (RuntimeException e){
            return errorMessage(e.getMessage());
        }

    }

    public ResponseApi<Address> getAddressById(int id){
        try {
            Address address =  repository.findById(id).orElseThrow(() -> new RuntimeException("Address not found"));
            return ResponseApi.<Address>builder()
                    .data(address)
                    .success(true)
                    .message("Address found")
                    .build();
        }catch (RuntimeException e){
            return errorMessage(e.getMessage());
        }
    }

    public ResponseApi<Address> create(Address address){
        try {
            return ResponseApi.<Address>builder()
                    .message("Address successfully saved")
                    .data(repository.save(address))
                    .success(true)
                    .build();
        }catch (RuntimeException e){
            return errorMessage(e.getMessage());
        }
    }

    public ResponseApi<Address> update(Address address, int id){
        try {
            Optional<Address> optionalAddress = repository.findById(id);
            Address oldAddress = optionalAddress.orElseThrow(() -> new RuntimeException("Address not found"));
            oldAddress.setRegionName(address.getRegionName());
            oldAddress.setDistrictName(address.getDistrictName());
            oldAddress.setHouseNumber(address.getHouseNumber());
            oldAddress = repository.save(oldAddress);

            return ResponseApi.<Address>builder()
                    .success(true)
                    .message("Address successfully updated")
                    .data(oldAddress)
                    .build();
        }catch (RuntimeException e){
           return errorMessage(e.getMessage());
        }
    }

    public ResponseApi<Address> delete(int id){
        try {
            Address address =  repository.findById(id).orElseThrow(() -> new RuntimeException("Address not found"));
            repository.delete(address);
            return ResponseApi.<Address>builder()
                    .success(true)
                    .message("Address successfully deleted")
                    .data(address)
                    .build();
        }catch (RuntimeException e){
            return errorMessage(e.getMessage());
        }
    }
}

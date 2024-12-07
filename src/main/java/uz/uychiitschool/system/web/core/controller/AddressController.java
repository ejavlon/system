package uz.uychiitschool.system.web.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uychiitschool.system.web.base.dto.ResponseApi;
import uz.uychiitschool.system.web.core.entity.Address;
import uz.uychiitschool.system.web.core.service.AddressService;

@RestController
@RequestMapping("/api/v1/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService service;

    @GetMapping("/{id}")
    public ResponseEntity<?> getAddressById(@PathVariable Integer id){
        ResponseApi<Address> responseApi = service.getAddressById(id);
        return ResponseEntity.status(responseApi.isSuccess() ? 200 : 404).body(responseApi);
    }

    @GetMapping
    public ResponseEntity<?> getAllAddresses(@RequestParam(required = false) Integer page,
                                             @RequestParam(required = false) Integer size){

        ResponseApi<Page<Address>> responseApi =
                service.getAllAddresses(page != null ? page : 0, size != null ? size : 10);
        return ResponseEntity.status(responseApi.isSuccess() ? 200 : 409).body(responseApi);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAddress(@PathVariable int id, @RequestBody Address address){
        ResponseApi<Address> responseApi = service.update(address, id);
        return ResponseEntity.status(responseApi.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(responseApi);
    }

    @PutMapping("/delete/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable int id){
        ResponseApi<Address> responseApi = service.delete(id);
        return ResponseEntity.status(responseApi.isSuccess() ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND).body(responseApi);
    }

}

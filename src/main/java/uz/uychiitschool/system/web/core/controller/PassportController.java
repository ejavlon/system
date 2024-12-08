package uz.uychiitschool.system.web.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uychiitschool.system.web.base.dto.ResponseApi;
import uz.uychiitschool.system.web.core.entity.Passport;
import uz.uychiitschool.system.web.core.service.PassportService;

@RestController
@RequestMapping("/api/v1/passport")
@RequiredArgsConstructor
public class PassportController {
    private final PassportService service;

    @GetMapping
    public ResponseEntity<?> getAllPassports(@RequestParam(required = false)Integer page,
                                             @RequestParam(required = false)Integer size) {
        ResponseApi<Page<Passport>> responseApi = service.getAllPassports(page != null ? (page - 1) : 0, size != null ? size : 10);
        return ResponseEntity.ok(responseApi);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAllPassports(@PathVariable Integer id) {
        ResponseApi<Passport> responseApi = service.getPassportById(id);
        return ResponseEntity.status(responseApi.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(responseApi);
    }

    @PostMapping
    public ResponseEntity<?> createPassport(@RequestBody Passport passport) {
        ResponseApi<Passport> responseApi = service.create(passport);
        return ResponseEntity.status(responseApi.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST).body(responseApi);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePassport(@PathVariable Integer id, @RequestBody Passport passport) {
        ResponseApi<Passport> responseApi = service.update(id, passport);
        return ResponseEntity.status(responseApi.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(responseApi);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePassport(@PathVariable Integer id) {
        ResponseApi<Passport> responseApi = service.delete(id);
        return ResponseEntity.status(responseApi.isSuccess() ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND).body(responseApi);
    }

}

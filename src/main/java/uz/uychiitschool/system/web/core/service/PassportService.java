package uz.uychiitschool.system.web.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uz.uychiitschool.system.web.base.dto.ResponseApi;
import uz.uychiitschool.system.web.base.service.BaseService;
import uz.uychiitschool.system.web.core.entity.Passport;
import uz.uychiitschool.system.web.core.exception.DataNotFoundException;
import uz.uychiitschool.system.web.core.repository.PassportRepository;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PassportService extends BaseService {
    private final PassportRepository repository;

    public ResponseApi<Page<Passport>> getAllPassports(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.sort(Passport.class).by(Passport::getSerial).ascending()
                    .and(Sort.sort(Passport.class).by(Passport::getNumber)).ascending());

            Page<Passport> passports = repository.findAll(pageable);
            return ResponseApi.<Page<Passport>>builder()
                    .message(String.format("Passports from %s to %s", page * size, page * size + size))
                    .success(true)
                    .data(passports)
                    .build();
        } catch (RuntimeException e) {
            return errorMessage(e.getMessage());
        }
    }


    public ResponseApi<Passport> getPassportById(Integer id) {
        try {
            Passport passport = repository.findById(id).orElseThrow(() -> new DataNotFoundException("Passport not found"));
            return ResponseApi.<Passport>builder()
                    .data(passport)
                    .message("Passport found")
                    .success(true)
                    .build();
        } catch (DataNotFoundException e) {
            return errorMessage(e.getMessage());
        }
    }

    public ResponseApi<Passport> create(Passport passport) {
        try {
            if (repository.existsBySerialAndNumber(passport.getSerial(), passport.getNumber()))
                return ResponseApi.<Passport>builder()
                        .message("Passport found")
                        .success(false)
                        .build();

            passport = repository.save(passport);
            return ResponseApi.<Passport>builder()
                    .message("Passport successfully created")
                    .success(true)
                    .data(passport)
                    .build();
        } catch (RuntimeException e) {
            return errorMessage(e.getMessage());
        }
    }

    public ResponseApi<Passport> update(int id, Passport newPassport) {
        try {
            Passport passport = repository.findById(id).orElseThrow(() -> new DataNotFoundException("Passport not found"));
            passport = updatePassportNotNullField(newPassport, passport);
            passport = repository.save(passport);

            return ResponseApi.<Passport>builder()
                    .data(passport)
                    .success(true)
                    .message("Passport successfully updated")
                    .build();
        } catch (DataNotFoundException e) {
            return errorMessage(e.getMessage());
        }
    }

    public Passport updatePassportNotNullField(Passport newPassport, Passport oldPassport) {
        if (newPassport.getSerial() != null) {
            oldPassport.setSerial(newPassport.getSerial());
        }

        if (newPassport.getNumber() != null) {
            oldPassport.setNumber(newPassport.getNumber());
        }
        return oldPassport;
    }

    public boolean existNewPassport(Passport newPassport, Passport oldPassport) {
        if(Objects.equals(newPassport.getSerial(), oldPassport.getSerial())
            && Objects.equals(newPassport.getNumber(), oldPassport.getNumber())) {
            return false;
        }

        return repository.existsBySerialAndNumber(newPassport.getSerial(), newPassport.getNumber());
    }


    public ResponseApi<Passport> delete(Integer id) {
        try {
            Passport passport = repository.findById(id).orElseThrow(() -> new DataNotFoundException("Passport not found"));
            repository.deleteById(id);

            return ResponseApi.<Passport>builder()
                    .data(passport)
                    .success(true)
                    .message("Passport successfully deleted")
                    .build();
        } catch (DataNotFoundException e) {
            return errorMessage(e.getMessage());
        }
    }

}

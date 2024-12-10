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
import uz.uychiitschool.system.web.core.entity.Passport;
import uz.uychiitschool.system.web.core.repository.PassportRepository;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PassportService {
    private final PassportRepository repository;

    public ResponseApi<Page<Passport>> getAllPassports(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.sort(Passport.class).by(Passport::getSerial).ascending()
                .and(Sort.sort(Passport.class).by(Passport::getNumber)).ascending());

        Page<Passport> passports = repository.findAll(pageable);
        return ResponseApi.createResponse(
                passports,
                String.format("Passports from %s to %s", page * size, Math.min((int) passports.getTotalElements(), page * size + size)),
                true
        );
    }


    public ResponseApi<Passport> getPassportById(Integer id) {
        Passport passport = findByIdOrThrow(id);
        return ResponseApi.createResponse(passport, "found", true);
    }


    public ResponseApi<Passport> create(Passport passport) {
        if (repository.existsBySerialAndNumber(passport.getSerial(), passport.getNumber()))
            throw new DuplicateEntityException("Passport already exists");

        passport = repository.save(passport);
        return ResponseApi.createResponse(passport, "Passport successfully created", true);
    }


    public ResponseApi<Passport> update(int id, Passport newPassport) {
        Passport passport = findByIdOrThrow(id);
        passport = updatePassportNotNullField(newPassport, passport);
        passport = repository.save(passport);
        return ResponseApi.createResponse(passport, "Passport successfully updated", true);
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

    public ResponseApi<Passport> delete(Integer id) {
        Passport passport = findByIdOrThrow(id);
        repository.deleteById(id);
        return ResponseApi.createResponse(passport, "Passport successfully created", true);
    }

    public Passport findByIdOrThrow(Integer id) {
        return repository.findById(id).orElseThrow(() -> new DataNotFoundException("Passport with ID " + id + " not found"));
    }

    public Passport createPassport(String serial, String number) {
        return Passport.builder()
                .serial(serial)
                .number(number)
                .build();
    }

    public Passport updateOrCreatePassport(StudentDto studentDto, Passport existingPassport) {
        if (existingPassport == null) {
            return createPassport(studentDto.getPassportSerial(), studentDto.getPassportNumber());
        }

        if ((studentDto.getPassportSerial() == null && studentDto.getPassportNumber() == null)
                || (Objects.equals(studentDto.getPassportSerial(), existingPassport.getSerial())
                && Objects.equals(studentDto.getPassportNumber(), existingPassport.getNumber()))) {
            return existingPassport;
        }

        Passport newPassport = createPassport(studentDto.getPassportSerial(), studentDto.getPassportNumber());
        if (newPassport.getSerial() == null)
            newPassport.setSerial(existingPassport.getSerial());

        if (newPassport.getNumber() == null)
            newPassport.setNumber(existingPassport.getNumber());

        if (!Objects.equals(newPassport.getSerial(), existingPassport.getSerial())
                || !Objects.equals(newPassport.getNumber(), existingPassport.getNumber())) {
            boolean exists = repository.existsBySerialAndNumber(existingPassport.getSerial(), existingPassport.getNumber());

            if (exists) throw new DuplicateEntityException("Passport already exists");

        }
        newPassport.setId(existingPassport.getId());
        return newPassport;
    }
}

package uz.uychiitschool.system.web.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uychiitschool.system.web.base.dto.ResponseApi;
import uz.uychiitschool.system.web.base.service.BaseService;
import uz.uychiitschool.system.web.core.dto.StudentDto;
import uz.uychiitschool.system.web.core.entity.Address;
import uz.uychiitschool.system.web.core.entity.Passport;
import uz.uychiitschool.system.web.core.entity.Student;
import uz.uychiitschool.system.web.core.enums.Gender;
import uz.uychiitschool.system.web.core.exception.DataNotFoundException;
import uz.uychiitschool.system.web.core.mapper.StudentMapper;
import uz.uychiitschool.system.web.core.repository.AddressRepository;
import uz.uychiitschool.system.web.core.repository.StudentRepository;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentService extends BaseService {
    private final StudentRepository repository;
    private final AddressRepository addressRepository;
    private final AddressService addressService;
    private final PassportService passportService;
    private final StudentMapper studentMapper;

    public ResponseApi<Page<Student>> getAllStudents(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.sort(Student.class)
                    .by(Student::getFirstName).ascending()
                    .and(Sort.sort(Student.class).by(Student::getLastName).ascending()));

            Page<Student> students = repository.findAll(pageable);
            return ResponseApi.<Page<Student>>builder()
                    .data(students)
                    .success(true)
                    .message(String.format("Students from %s to %s", page * size, page * size + size))
                    .build();
        } catch (RuntimeException e) {
            return errorMessage(e.getMessage());
        }
    }

    public ResponseApi<Student> getStudentById(int id) {
        try {
            Student student = repository.findById(id).orElseThrow(() -> new DataNotFoundException("Student not found"));
            return ResponseApi.<Student>builder()
                    .data(student)
                    .success(true)
                    .message("Student found")
                    .build();
        } catch (DataNotFoundException e) {
            return errorMessage(e.getMessage());
        }
    }

    @Transactional
    public ResponseApi<Student> createStudent(StudentDto studentDto) {
        try {
            Optional<Address> optionalAddress = addressRepository.findByRegionNameAndDistrictNameAndStreetNameAndHouseNumber(
                    studentDto.getRegionName(),
                    studentDto.getDistrictName(),
                    studentDto.getStreetName(),
                    studentDto.getHouseNumber()
            );
            Address address = optionalAddress.orElse(null);

            if (address == null) {
                address = Address.builder()
                        .regionName(studentDto.getRegionName())
                        .districtName(studentDto.getDistrictName())
                        .streetName(studentDto.getStreetName())
                        .houseNumber(studentDto.getHouseNumber())
                        .build();
            }

            Passport passport = Passport.builder()
                    .serial(studentDto.getPassportSerial())
                    .number(studentDto.getPassportNumber())
                    .build();

            Student student = Student.builder()
                    .firstName(studentDto.getFirstName())
                    .lastName(studentDto.getLastName())
                    .birthday(studentDto.getBirthDate())
                    .gender(Gender.valueOf(studentDto.getGender()))
                    .passport(passport)
                    .fatherName(studentDto.getFatherName())
                    .phoneNumber(studentDto.getPhoneNumber())
                    .fatherPhoneNumber(studentDto.getFatherPhoneNumber())
                    .address(address)
                    .build();

            student = repository.save(student);
            return ResponseApi.<Student>builder()
                    .data(student)
                    .success(true)
                    .message("Student successfully created")
                    .build();
        } catch (RuntimeException e) {
            return errorMessage(e.getMessage());
        }
    }

    @Transactional
    public ResponseApi<Student> updateStudentById(int id, StudentDto studentDto) {
        try {
            Student student = repository.findById(id).orElseThrow(() -> new DataNotFoundException("Student not found"));

            Address oldAddress = student.getAddress();
            Address newAddress = Address.builder()
                    .regionName(studentDto.getRegionName())
                    .districtName(studentDto.getDistrictName())
                    .streetName(studentDto.getStreetName())
                    .houseNumber(studentDto.getHouseNumber())
                    .build();

            newAddress = addressService.updateAddressNotNullField(newAddress, oldAddress);

            Optional<Address> optionalAddress = addressRepository.findByRegionNameAndDistrictNameAndStreetNameAndHouseNumber(
                    newAddress.getRegionName(),
                    newAddress.getDistrictName(),
                    newAddress.getStreetName(),
                    newAddress.getHouseNumber()
            );

            if (optionalAddress.isPresent()) {
                newAddress = optionalAddress.get();
            }

            Passport oldPassport = student.getPassport();
            Passport newPassport = Passport.builder()
                    .serial(studentDto.getPassportSerial())
                    .number(studentDto.getPassportNumber())
                    .build();

            newPassport = passportService.updatePassportNotNullField(newPassport, oldPassport);
            if (passportService.existNewPassport(newPassport, oldPassport))
                return ResponseApi.<Student>builder()
                        .message(String.format("%s %s passport exist", newPassport.getSerial(), newPassport.getNumber()))
                        .success(false)
                        .build();

            studentMapper.updateStudentFromDto(studentDto, student);
            student.setAddress(newAddress);
            student.setPassport(newPassport);
            student = repository.save(student);

            return ResponseApi.<Student>builder()
                    .data(student)
                    .success(true)
                    .message("Student successfully updated")
                    .build();
        } catch (DataNotFoundException e) {
            return errorMessage(e.getMessage());
        }
    }

    public ResponseApi<Student> deleteStudentById(int id) {
        try {
            Student student = repository.findById(id).orElseThrow(() -> new DataNotFoundException("Student not found"));
            repository.deleteById(id);
            return ResponseApi.<Student>builder()
                    .data(student)
                    .success(true)
                    .message("Student deleted successfully")
                    .build();
        } catch (DataNotFoundException e) {
            return errorMessage(e.getMessage());
        }
    }
}

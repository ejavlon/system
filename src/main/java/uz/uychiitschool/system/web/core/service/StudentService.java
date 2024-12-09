package uz.uychiitschool.system.web.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uychiitschool.system.web.base.dto.ResponseApi;
import uz.uychiitschool.system.web.base.exception.DataNotFoundException;
import uz.uychiitschool.system.web.core.dto.StudentDto;
import uz.uychiitschool.system.web.core.entity.Address;
import uz.uychiitschool.system.web.core.entity.Passport;
import uz.uychiitschool.system.web.core.entity.Student;
import uz.uychiitschool.system.web.core.enums.Gender;
import uz.uychiitschool.system.web.core.mapper.StudentMapper;
import uz.uychiitschool.system.web.core.repository.AddressRepository;
import uz.uychiitschool.system.web.core.repository.StudentRepository;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository repository;
    private final AddressRepository addressRepository;
    private final AddressService addressService;
    private final PassportService passportService;
    private final StudentMapper studentMapper;

    public ResponseApi<Page<Student>> getAllStudents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.sort(Student.class)
                .by(Student::getFirstName).ascending()
                .and(Sort.sort(Student.class).by(Student::getLastName).ascending()));

        Page<Student> students = repository.findAll(pageable);
        return ResponseApi.<Page<Student>>builder()
                .data(students)
                .success(true)
                .message(String.format("Students from %s to %s", page * size, Math.min((int) students.getTotalElements(), page * size + size)))
                .build();
    }

    public ResponseApi<Student> getStudentById(int id) {
        Student student = repository.findById(id).orElseThrow(() -> new DataNotFoundException("Student not found"));
        return ResponseApi.<Student>builder()
                .data(student)
                .success(true)
                .message("Student found")
                .build();
    }

    @Transactional
    public ResponseApi<Student> createStudent(StudentDto studentDto) {
        Address newAddress = addressService.updateOrCreateAddress(studentDto, null);
        Address findFullFieldAddress = addressService.findByFullField(newAddress);

        if (findFullFieldAddress != null)
            newAddress.setId(findFullFieldAddress.getId());

        Passport passport = passportService.updateOrCreatePassport(studentDto,null);

        Student student = createStudentFromStudentDto(studentDto);
        student.setAddress(newAddress);
        student.setPassport(passport);

        student = repository.save(student);
        return ResponseApi.<Student>builder()
                .data(student)
                .success(true)
                .message("Student successfully created")
                .build();
    }

    @Transactional
    public ResponseApi<Student> updateStudentById(int id, StudentDto studentDto) {
        Student student = repository.findById(id).orElseThrow(() -> new DataNotFoundException("Student not found"));

        Address oldAddress = student.getAddress();
        Address newAddress = addressService.updateOrCreateAddress(studentDto, oldAddress);

        Address findByFullFieldAddress = addressService.findByFullField(newAddress);
        if (findByFullFieldAddress != null)
            newAddress.setId(findByFullFieldAddress.getId());

        Passport oldPassport = student.getPassport();
        Passport newPassport = passportService.updateOrCreatePassport(studentDto,oldPassport);

        student = updateOrCreateStudent(studentDto, student);
        student.setAddress(newAddress);
        student.setPassport(newPassport);
        student = repository.save(student);

        return ResponseApi.<Student>builder()
                .data(student)
                .success(true)
                .message("Student successfully updated")
                .build();
    }

    public ResponseApi<Student> deleteStudentById(int id) {
        Student student = repository.findById(id).orElseThrow(() -> new DataNotFoundException("Student not found"));
        repository.deleteById(id);
        return ResponseApi.<Student>builder()
                .data(student)
                .success(true)
                .message("Student deleted successfully")
                .build();
    }

    public Student createStudentFromStudentDto(StudentDto studentDto) {
        return Student.builder()
                .firstName(studentDto.getFirstName())
                .lastName(studentDto.getLastName())
                .birthday(studentDto.getBirthDate())
                .gender(Gender.valueOf(studentDto.getGender()))
                .fatherName(studentDto.getFatherName())
                .phoneNumber(studentDto.getPhoneNumber())
                .fatherPhoneNumber(studentDto.getFatherPhoneNumber())
                .build();
    }

    public Student updateOrCreateStudent(StudentDto studentDto, Student existingStudent) {
        if (existingStudent == null) {
            return createStudentFromStudentDto(studentDto);
        }
        studentMapper.updateStudentFromDto(studentDto, existingStudent);
        return existingStudent;
    }
}
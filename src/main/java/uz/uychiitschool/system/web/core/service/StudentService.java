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
import uz.uychiitschool.system.web.base.enums.Gender;
import uz.uychiitschool.system.web.core.repository.StudentRepository;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository repository;
    private final AddressService addressService;
    private final PassportService passportService;

    public ResponseApi<Page<Student>> getAllStudents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.sort(Student.class)
                .by(Student::getFirstName).ascending()
                .and(Sort.sort(Student.class).by(Student::getLastName).ascending()));

        Page<Student> students = repository.findAll(pageable);
        return ResponseApi.createResponse(
                students,
                String.format("Students from %s to %s", page * size, Math.min((int) students.getTotalElements(), page * size + size)),
                true
        );
    }

    public ResponseApi<Student> getStudentById(int id) {
        Student student = findStudentByIdOrThrow(id);
        return ResponseApi.createResponse(student, "Student found", true);
    }

    @Transactional
    public ResponseApi<Student> createStudent(StudentDto studentDto) {
        Address newAddress = addressService.updateOrCreateAddress(studentDto, null);

        Passport passport = passportService.updateOrCreatePassport(studentDto,null);

        Student student = updateOrCreateStudent(studentDto,null);
        student.setAddress(newAddress);
        student.setPassport(passport);

        student = repository.save(student);
        return ResponseApi.createResponse(student, "Student successfully created", true);
    }

    @Transactional
    public ResponseApi<Student> updateStudentById(int id, StudentDto studentDto) {
        Student student = findStudentByIdOrThrow(id);

        Address oldAddress = student.getAddress();
        Address newAddress = addressService.updateOrCreateAddress(studentDto, oldAddress);

        Passport oldPassport = student.getPassport();
        Passport newPassport = passportService.updateOrCreatePassport(studentDto,oldPassport);

        student = updateOrCreateStudent(studentDto, student);
        student.setAddress(newAddress);
        student.setPassport(newPassport);
        student = repository.save(student);

        return ResponseApi.createResponse(student, "Student successfully updated", true);
    }

    public ResponseApi<Student> deleteStudentById(int id) {
        Student student = findStudentByIdOrThrow(id);
        repository.deleteById(id);
        return ResponseApi.createResponse(student, "Student successfully deleted", true);
    }

    public Student findStudentByIdOrThrow(int id) {
        return repository.findById(id).orElseThrow(() -> new DataNotFoundException("Student not found"));
    }

    public Student createStudentFromStudentDto(StudentDto studentDto) {
        Gender gender = Gender.fromString(studentDto.getGender());
        return Student.builder()
                .firstName(studentDto.getFirstName())
                .lastName(studentDto.getLastName())
                .birthday(studentDto.getBirthDate())
                .gender(gender)
                .fatherName(studentDto.getFatherName())
                .phoneNumber(studentDto.getPhoneNumber())
                .fatherPhoneNumber(studentDto.getFatherPhoneNumber())
                .build();
    }

    public Student updateOrCreateStudent(StudentDto studentDto, Student existingStudent) {
        if (existingStudent == null) {
            return createStudentFromStudentDto(studentDto);
        }
        if (studentDto.getFirstName() != null)
            existingStudent.setFirstName(studentDto.getFirstName());

        if (studentDto.getLastName() != null)
            existingStudent.setLastName(studentDto.getLastName());

        if (studentDto.getBirthDate() != null)
            existingStudent.setBirthday(studentDto.getBirthDate());

        if (studentDto.getFatherName() != null)
            existingStudent.setFatherName(studentDto.getFatherName());

        if (studentDto.getPhoneNumber() != null)
            existingStudent.setPhoneNumber(studentDto.getPhoneNumber());

        if (studentDto.getFatherPhoneNumber() != null)
            existingStudent.setFatherPhoneNumber(studentDto.getFatherPhoneNumber());

        if (Objects.nonNull(studentDto.getGender())){
            existingStudent.setGender(Gender.fromString(studentDto.getGender()));
        }
        return existingStudent;
    }
}
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
import uz.uychiitschool.system.web.core.entity.Student;
import uz.uychiitschool.system.web.core.repository.AddressRepository;
import uz.uychiitschool.system.web.core.repository.StudentRepository;

@Service
@RequiredArgsConstructor
public class StudentService extends BaseService {
    private final StudentRepository repository;
    private final AddressRepository addressRepository;

    public ResponseApi<Page<Student>> getAllStudents(int page, int size){
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.sort(Student.class)
                    .by(Student::getFirstName).ascending()
                    .and(Sort.sort(Student.class).by(Student::getLastName).ascending()));

            Page<Student> students = repository.findAll(pageable);
            return ResponseApi.<Page<Student>>builder()
                    .data(students)
                    .success(true)
                    .message(String.format("Students from %s to %s", page*size, page*size + size))
                    .build();
        }catch (RuntimeException e){
            return errorMessage(e.getMessage());
        }
    }

    public ResponseApi<Student> getStudentById(int id){
        try {
            Student student = repository.findById(id).orElseThrow(() -> new RuntimeException("Student not found"));
            return ResponseApi.<Student>builder()
                    .data(student)
                    .success(true)
                    .message("Student found")
                    .build();
        }catch (RuntimeException e){
            return errorMessage(e.getMessage());
        }
    }

    @Transactional
    public ResponseApi<Student> createStudent(StudentDto studentDto){
        try {
            Address address = Address.builder()
                    .regionName(studentDto.getRegionName())
                    .districtName(studentDto.getDistrictName())
                    .streetName(studentDto.getStreetName())
                    .houseNumber(studentDto.getHouseNumber())
                    .build();
            address = addressRepository.save(address);

            Student student =  Student.builder()
                    .firstName(studentDto.getFirstName())
                    .lastName(studentDto.getLastName())
                    .fatherName(studentDto.getFatherName())
                    .phoneNumber(studentDto.getPhoneNumber())
                    .fatherPhoneNumber(studentDto.getFatherPhoneNumber())
                    .address(address)
                    .build();

            student = repository.save(student);

            student = repository.save(student);
            return ResponseApi.<Student>builder()
                    .data(student)
                    .success(true)
                    .message("Student successfully created")
                    .build();
        }catch (RuntimeException e){
            return errorMessage(e.getMessage());
        }
    }

    @Transactional
    public ResponseApi<Student> updateStudentById(int id, StudentDto studentDto){
        try {
            Student  student = repository.findById(id).orElseThrow(() -> new RuntimeException("Student not found"));

            Address address = student.getAddress();
            address.setRegionName(studentDto.getRegionName());
            address.setDistrictName(studentDto.getDistrictName());
            address.setStreetName(studentDto.getStreetName());
            address.setHouseNumber(studentDto.getHouseNumber());

            student.setFirstName(studentDto.getFirstName());
            student.setLastName(studentDto.getLastName());
            student.setFatherName(studentDto.getFatherName());
            student.setPhoneNumber(studentDto.getPhoneNumber());
            student.setFatherPhoneNumber(studentDto.getFatherPhoneNumber());
            student.setAddress(address);

            student = repository.save(student);

            return ResponseApi.<Student>builder()
                    .data(student)
                    .success(true)
                    .message("Student successfully updated")
                    .build();
        }catch (RuntimeException e){
            return errorMessage(e.getMessage());
        }
    }

    public ResponseApi<Student> deleteStudentById(int id){
        try {
            Student student = repository.findById(id).orElseThrow(() -> new RuntimeException("Student not found"));
            repository.deleteById(id);
            return ResponseApi.<Student>builder()
                    .data(student)
                    .success(true)
                    .message("Student deleted successfully")
                    .build();
        }catch (RuntimeException e){
            return errorMessage(e.getMessage());
        }
    }
}

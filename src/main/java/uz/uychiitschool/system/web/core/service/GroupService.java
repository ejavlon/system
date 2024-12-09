package uz.uychiitschool.system.web.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uychiitschool.system.web.base.dto.ResponseApi;
import uz.uychiitschool.system.web.base.entity.User;
import uz.uychiitschool.system.web.base.exception.DataNotFoundException;
import uz.uychiitschool.system.web.base.repository.UserRepository;
import uz.uychiitschool.system.web.core.dto.GroupDto;
import uz.uychiitschool.system.web.core.entity.Course;
import uz.uychiitschool.system.web.core.entity.Group;
import uz.uychiitschool.system.web.core.enums.GroupStatus;
import uz.uychiitschool.system.web.core.mapper.GroupMapper;
import uz.uychiitschool.system.web.core.repository.CourseRepository;
import uz.uychiitschool.system.web.core.repository.GroupRepository;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository repository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final GroupMapper mapper;

    public ResponseApi<Page<Group>> getAllGroups(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.sort(Group.class)
                .by(Group::getStartDate).ascending()
                .and(Sort.sort(Group.class).by(Group::getName).ascending()));

        Page<Group> groups = repository.findAll(pageable);
        return ResponseApi.<Page<Group>>builder()
                .data(groups)
                .success(true)
                .message(String.format("Groups from %s to %s", page * size, Math.min((int)groups.getTotalElements(), page * size + size)))
                .build();
    }

    public ResponseApi<Group> getGroupById(int id) {
        Group group = repository.findById(id).orElseThrow(() -> new DataNotFoundException("Group not found"));
        return ResponseApi.<Group>builder()
                .data(group)
                .success(true)
                .message("Group found")
                .build();
    }

    @Transactional
    public ResponseApi<Group> createGroup(GroupDto groupDto) {
        Course course = courseRepository.findById(groupDto.getCourseId()).orElseThrow(() -> new DataNotFoundException("Course not found"));
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now();
        endDate = endDate.plusMonths(course.getDuration());

        User teacher = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Group group = Group.builder()
                .name(groupDto.getName())
                .teacher(teacher)
                .size(groupDto.getSize())
                .currentSize(groupDto.getCurrentSize())
                .status(GroupStatus.valueOf(groupDto.getStatus()))
                .course(course)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        group = repository.save(group);
        return ResponseApi.<Group>builder()
                .data(group)
                .success(true)
                .message("Group successfully created")
                .build();
    }

    @Transactional
    public ResponseApi<Group> createGroupSuperAdmin(GroupDto groupDto) {
        User teacher = userRepository.findById(groupDto.getTeacherId()).orElseThrow(() -> new DataNotFoundException("Teacher not found"));
        Course course = courseRepository.findById(groupDto.getCourseId()).orElseThrow(() -> new DataNotFoundException("Course not found"));

        Group group = Group.builder()
                .name(groupDto.getName())
                .teacher(teacher)
                .size(groupDto.getSize())
                .currentSize(groupDto.getCurrentSize())
                .status(GroupStatus.valueOf(groupDto.getStatus()))
                .startDate(groupDto.getStartDate())
                .endDate(groupDto.getEndDate())
                .course(course)
                .build();

        group = repository.save(group);
        return ResponseApi.<Group>builder()
                .data(group)
                .success(true)
                .message("Group successfully created")
                .build();
    }

    @Transactional
    public ResponseApi<Group> updateGroupById(int id, GroupDto groupDto) {
        Group group = repository.findById(id).orElseThrow(() -> new DataNotFoundException("Group not found"));
        User teacher = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Course course = courseRepository.findById(groupDto.getCourseId()).orElseThrow(() -> new DataNotFoundException("Course not found"));

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now();
        endDate = endDate.plusMonths(course.getDuration());

        mapper.updateGroupFromDto(groupDto, group);
        group.setTeacher(teacher);
        group.setCourse(course);
        group.setStartDate(startDate);
        group.setEndDate(endDate);
        group = repository.save(group);

        return ResponseApi.<Group>builder()
                .data(group)
                .success(true)
                .message("Group successfully updated")
                .build();
    }

    @Transactional
    public ResponseApi<Group> updateGroupByIdWithSuperAdmin(int id, GroupDto groupDto) {
        Group group = repository.findById(id).orElseThrow(() -> new DataNotFoundException("Group not found"));
        User teacher = userRepository.findById(groupDto.getTeacherId()).orElseThrow(() -> new DataNotFoundException("Teacher not found"));
        Course course = courseRepository.findById(groupDto.getCourseId()).orElseThrow(() -> new DataNotFoundException("Course not found"));

        mapper.updateGroupFromDto(groupDto, group);
        group.setTeacher(teacher);
        group.setCourse(course);
        group = repository.save(group);

        return ResponseApi.<Group>builder()
                .data(group)
                .success(true)
                .message("Group successfully updated")
                .build();
    }

    public ResponseApi<Group> deleteGroupById(int id) {
        Group group = repository.findById(id).orElseThrow(() -> new DataNotFoundException("Group not found"));
        repository.deleteById(id);
        return ResponseApi.<Group>builder()
                .data(group)
                .success(true)
                .message("Group successfully deleted")
                .build();
    }
}
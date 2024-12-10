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
import uz.uychiitschool.system.web.base.service.UserService;
import uz.uychiitschool.system.web.core.dto.GroupDto;
import uz.uychiitschool.system.web.core.entity.Course;
import uz.uychiitschool.system.web.core.entity.Group;
import uz.uychiitschool.system.web.core.enums.GroupStatus;
import uz.uychiitschool.system.web.core.repository.GroupRepository;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository repository;
    private final UserService userService;
    private final CourseService courseService;

    public ResponseApi<Page<Group>> getAllGroups(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.sort(Group.class)
                .by(Group::getStartDate).ascending()
                .and(Sort.sort(Group.class).by(Group::getName).ascending()));

        Page<Group> groups = repository.findAll(pageable);
        return ResponseApi.createResponse(
                groups,
                String.format("Groups from %s to %s", page * size, Math.min((int) groups.getTotalElements(), page * size + size)),
                true
        );
    }

    public ResponseApi<Group> getGroupById(int id) {
        Group group = findGroupByIdOrThrow(id);
        return ResponseApi.createResponse(group, "group found", true);
    }

    @Transactional
    public ResponseApi<Group> create(GroupDto groupDto) {
        Course course = courseService.findCourseByIdOrThrow(groupDto.getCourseId());

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusMonths(course.getDuration());

        User teacher = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Group group = createOrUpdateGroup(groupDto, null);
        group.setTeacher(teacher);
        group.setCourse(course);
        group.setStartDate(startDate);
        group.setEndDate(endDate);

        return ResponseApi.createResponse(repository.save(group), "group successfully created", true);
    }

    @Transactional
    public ResponseApi<Group> createGroupSuperAdmin(GroupDto groupDto) {
        User teacher = userService.findUserByIdOrUsernameOrThrow(groupDto.getTeacherId(), null);
        Course course = courseService.findCourseByIdOrThrow(groupDto.getCourseId());

        Group group = createOrUpdateGroup(groupDto, null);
        group.setTeacher(teacher);
        group.setCourse(course);

        group = repository.save(group);
        return ResponseApi.createResponse(group, "group successfully created", true);
    }

    @Transactional
    public ResponseApi<Group> updateGroupById(int id, GroupDto groupDto) {
        Group group = findGroupByIdOrThrow(id);
        User teacher = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Course course = courseService.findCourseByIdOrThrow(groupDto.getCourseId());

        LocalDateTime startDate = groupDto.getStartDate();
        LocalDateTime endDate = startDate.plusMonths(course.getDuration());

        group = createOrUpdateGroup(groupDto, group);
        group.setTeacher(teacher);
        group.setCourse(course);
        group.setStartDate(startDate);
        group.setEndDate(endDate);
        return ResponseApi.createResponse(repository.save(group), "group successfully updated", true);
    }

    @Transactional
    public ResponseApi<Group> updateGroupByIdWithSuperAdmin(int id, GroupDto groupDto) {
        Group group = findGroupByIdOrThrow(id);
        User teacher = userService.findUserByIdOrUsernameOrThrow(groupDto.getTeacherId(), null);
        Course course = courseService.findCourseByIdOrThrow(groupDto.getCourseId());

        group = createOrUpdateGroup(groupDto,group);
        group.setTeacher(teacher);
        group.setCourse(course);
        return ResponseApi.createResponse(repository.save(group), "group successfully updated", true);
    }

    public ResponseApi<Group> deleteGroupById(int id) {
        Group group = findGroupByIdOrThrow(id);
        repository.deleteById(id);
        return ResponseApi.createResponse(group, "group successfully deleted", true);
    }

    public Group findGroupByIdOrThrow(int id) {
        return repository.findById(id).orElseThrow(() -> new DataNotFoundException("Group not found"));
    }

    public Group createGroup(GroupDto groupDto) {
        return Group.builder()
                .name(groupDto.getName())
                .size(groupDto.getSize())
                .currentSize(groupDto.getCurrentSize())
                .status(GroupStatus.valueOf(groupDto.getStatus()))
                .startDate(groupDto.getStartDate())
                .endDate(groupDto.getEndDate())
                .build();
    }

    public Group createOrUpdateGroup(GroupDto groupDto, Group existingGroup) {
        if (existingGroup == null) {
            return createGroup(groupDto);
        }

        if (groupDto.getName() != null)
            existingGroup.setName(groupDto.getName());

        if (groupDto.getSize() != null)
            existingGroup.setSize(groupDto.getSize());

        if (groupDto.getCurrentSize() != null)
            existingGroup.setCurrentSize(groupDto.getCurrentSize());

        if (groupDto.getStatus() != null)
            existingGroup.setStatus(GroupStatus.fromString(groupDto.getStatus()));

        return existingGroup;
    }
}
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
import uz.uychiitschool.system.web.base.repository.UserRepository;
import uz.uychiitschool.system.web.base.service.BaseService;
import uz.uychiitschool.system.web.core.dto.GroupDto;
import uz.uychiitschool.system.web.core.entity.Group;
import uz.uychiitschool.system.web.core.enums.CourseType;
import uz.uychiitschool.system.web.core.enums.GroupStatus;
import uz.uychiitschool.system.web.core.mapper.GroupMapper;
import uz.uychiitschool.system.web.core.repository.GroupRepository;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class GroupService extends BaseService {

    private final GroupRepository repository;
    private final UserRepository userRepository;
    private final GroupMapper mapper;

    public ResponseApi<Page<Group>> getAllGroups(int page, int size){
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.sort(Group.class)
                    .by(Group::getStartDate).ascending()
                    .and(Sort.sort(Group.class).by(Group::getName).ascending()));

            Page<Group> groups = repository.findAll(pageable);
            return ResponseApi.<Page<Group>>builder()
                    .data(groups)
                    .success(true)
                    .message("All groups")
                    .build();
        }catch (RuntimeException e){
            return errorMessage(e.getMessage());
        }
    }

    public ResponseApi<Group> getGroupById(int id){
        try {
            Group group = repository.findById(id).orElseThrow(() -> new RuntimeException("Group not found"));
            return ResponseApi.<Group>builder()
                    .data(group)
                    .success(true)
                    .message("Group found")
                    .build();
        }catch (RuntimeException e){
            return errorMessage(e.getMessage());
        }
    }

    public ResponseApi<Group> createGroup(GroupDto groupDto){
        try {
            CourseType courseType = CourseType.valueOf(groupDto.getCourseType());
            LocalDateTime startDate = LocalDateTime.now();
            LocalDateTime endDate = LocalDateTime.now();
            endDate = endDate.plusMonths(courseType.getMonth());

            User teacher = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Group group =  Group.builder()
                    .name(groupDto.getName())
                    .teacher(teacher)
                    .size(groupDto.getSize())
                    .currentSize(groupDto.getCurrentSize())
                    .status(GroupStatus.valueOf(groupDto.getStatus()))
                    .courseType(courseType)
                    .startDate(startDate)
                    .endDate(endDate)
                    .build();

            group = repository.save(group);
            return ResponseApi.<Group>builder()
                    .data(group)
                    .success(true)
                    .message("Group successfully created")
                    .build();
        }catch (RuntimeException e){
            return errorMessage(e.getMessage());
        }
    }

    @Transactional
    public ResponseApi<Group> createGroupSuperAdmin(GroupDto groupDto) {
        try {
            User teacher = userRepository.findById(groupDto.getTeacherId()).orElseThrow(() -> new RuntimeException("Teacher not found"));
            Group group =  Group.builder()
                    .name(groupDto.getName())
                    .teacher(teacher)
                    .size(groupDto.getSize())
                    .currentSize(groupDto.getCurrentSize())
                    .status(GroupStatus.valueOf(groupDto.getStatus()))
                    .startDate(groupDto.getStartDate())
                    .endDate(groupDto.getEndDate())
                    .courseType(CourseType.valueOf(groupDto.getCourseType()))
                    .build();

            group = repository.save(group);
            return ResponseApi.<Group>builder()
                    .data(group)
                    .success(true)
                    .message("Group successfully created")
                    .build();
        }catch (RuntimeException e){
            return errorMessage(e.getMessage());
        }
    }

    @Transactional
    public ResponseApi<Group> updateGroupById(int id, GroupDto groupDto){
        try {
            Group group = repository.findById(id).orElseThrow(() -> new RuntimeException("Group not found"));
            User teacher = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            mapper.updateGroupFromDto(groupDto, group);
            group.setTeacher(teacher);
            group = repository.save(group);

            return ResponseApi.<Group>builder()
                    .data(group)
                    .success(true)
                    .message("Group successfully updated")
                    .build();
        }catch (RuntimeException e){
            return errorMessage(e.getMessage());
        }
    }

    @Transactional
    public ResponseApi<Group> updateGroupByIdWithSuperAdmin(int id, GroupDto groupDto){
        try {
            Group group = repository.findById(id).orElseThrow(() -> new RuntimeException("Group not found"));
            User teacher = userRepository.findById(groupDto.getTeacherId()).orElseThrow(() -> new RuntimeException("Teacher not found"));

            mapper.updateGroupFromDto(groupDto, group);
            group.setTeacher(teacher);
            group = repository.save(group);

            return ResponseApi.<Group>builder()
                    .data(group)
                    .success(true)
                    .message("Group successfully updated")
                    .build();
        }catch (RuntimeException e){
            return errorMessage(e.getMessage());
        }
    }

    public ResponseApi<Group> deleteGroupById(int id){
        try {
            Group group = repository.findById(id).orElseThrow(() -> new RuntimeException("Group not found"));
            repository.delete(group);
            return ResponseApi.<Group>builder()
                    .data(group)
                    .success(true)
                    .message("Group successfully deleted")
                    .build();
        }catch (RuntimeException e){
            return errorMessage(e.getMessage());
        }
    }
}

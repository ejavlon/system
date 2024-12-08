package uz.uychiitschool.system.web.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.uychiitschool.system.web.base.dto.ResponseApi;
import uz.uychiitschool.system.web.core.dto.GroupDto;
import uz.uychiitschool.system.web.core.entity.Group;
import uz.uychiitschool.system.web.core.service.GroupService;

@RestController
@RequestMapping("/api/v1/group")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService service;

    @GetMapping
    public ResponseEntity<?> getAllGroups(@RequestParam(required = false) Integer page,
                                          @RequestParam(required = false) Integer size){
        ResponseApi<Page<Group>> responseApi = service.getAllGroups(page != null ? page : 0, size != null ? size : 10);
        return ResponseEntity.status(responseApi.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(responseApi);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGroupById(@PathVariable int id){
        ResponseApi<Group> responseApi = service.getGroupById(id);
        return ResponseEntity.status(responseApi.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(responseApi);
    }

    @PostMapping
    public ResponseEntity<?> createGroup(@Valid @RequestBody GroupDto groupDto){
        ResponseApi<Group> responseApi = service.createGroup(groupDto);
        return ResponseEntity.status(responseApi.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST).body(responseApi);
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createGroupSuperAdmin(@Valid @RequestBody GroupDto groupDto){
        ResponseApi<Group> responseApi = service.createGroupSuperAdmin(groupDto);
        return ResponseEntity.status(responseApi.isSuccess() ? HttpStatus.CREATED : HttpStatus.NOT_FOUND).body(responseApi);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGroupById(@PathVariable int id, @Valid @RequestBody GroupDto groupDto){
        ResponseApi<Group> responseApi = service.updateGroupById(id,groupDto);
        return ResponseEntity.status(responseApi.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(responseApi);
    }


    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateGroupByIdWithSuperAdmin(@PathVariable int id, @Valid @RequestBody GroupDto groupDto){
        ResponseApi<Group> responseApi = service.updateGroupByIdWithSuperAdmin(id,groupDto);
        return ResponseEntity.status(responseApi.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(responseApi);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroupById(@PathVariable int id){
        ResponseApi<Group> responseApi = service.deleteGroupById(id);
        return ResponseEntity.status(responseApi.isSuccess() ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND).body(responseApi);
    }
}

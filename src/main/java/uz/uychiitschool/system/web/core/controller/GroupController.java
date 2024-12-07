package uz.uychiitschool.system.web.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
        return ResponseEntity.status(responseApi.isSuccess() ? 200 : 400).body(responseApi);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGroupById(@PathVariable int id){
        ResponseApi<Group> responseApi = service.getGroupById(id);
        return ResponseEntity.status(responseApi.isSuccess() ? 200 : 404).body(responseApi);
    }

    @PostMapping
    public ResponseEntity<?> createGroup(@RequestBody GroupDto groupDto){
        ResponseApi<Group> responseApi = service.createGroup(groupDto);
        return ResponseEntity.status(responseApi.isSuccess() ? 201 : 404).body(responseApi);
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createGroupSuperAdmin(@RequestBody GroupDto groupDto){
        ResponseApi<Group> responseApi = service.createGroupSuperAdmin(groupDto);
        return ResponseEntity.status(responseApi.isSuccess() ? 201 : 404).body(responseApi);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGroupById(@PathVariable int id, @RequestBody GroupDto groupDto){
        ResponseApi<Group> responseApi = service.updateGroupById(id,groupDto);
        return ResponseEntity.status(responseApi.isSuccess() ? 200 : 404).body(responseApi);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroupById(@PathVariable int id){
        ResponseApi<Group> responseApi = service.deleteGroupById(id);
        return ResponseEntity.status(responseApi.isSuccess() ? 204 : 404).body(responseApi);
    }
}

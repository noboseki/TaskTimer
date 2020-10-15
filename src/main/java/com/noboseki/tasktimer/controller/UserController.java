package com.noboseki.tasktimer.controller;

import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.security.perms.AdminPermission;
import com.noboseki.tasktimer.security.perms.UserPermission;
import com.noboseki.tasktimer.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("user")
@RestController
public class UserController {

    private UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("create")
    public ResponseEntity<?> create(User.UserDto dto) {
        return service.create(dto);
    }

    @UserPermission
    @GetMapping("get/{uuid}")
    public ResponseEntity<?> get(@PathVariable UUID uuid) {
        return service.get(uuid);
    }
    @UserPermission
    @PutMapping("update")
    public ResponseEntity<?> update(User.UserDto dto) {
        return service.update(dto);
    }

    @AdminPermission
    @DeleteMapping("delete/{uuid}")
    public ResponseEntity<?> delete(@PathVariable UUID uuid) {
        return service.delete(uuid);
    }
}

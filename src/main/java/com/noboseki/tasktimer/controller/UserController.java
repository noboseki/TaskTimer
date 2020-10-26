package com.noboseki.tasktimer.controller;

import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.playload.UserCreateRequest;
import com.noboseki.tasktimer.security.perms.AdminPermission;
import com.noboseki.tasktimer.security.perms.UserPermission;
import com.noboseki.tasktimer.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping("create")
    public ResponseEntity<?> create(@RequestBody UserCreateRequest request) {
        return service.create(request);
    }

    @UserPermission
    @GetMapping("get")
    public ResponseEntity<?> get(@AuthenticationPrincipal User user) {
        return service.get(user.getEmail(), user.getPassword());
    }

    @AdminPermission
    @GetMapping("get/{email}")
    public ResponseEntity<?> getByEmail(@PathVariable String email) {
        return service.getByEmail(email);
    }

    @UserPermission
    @PutMapping("update/name/{name}")
    public ResponseEntity<?> updateName(@PathVariable String name,
                                        @AuthenticationPrincipal User user) {
        return service.updateName(name, user);
    }

    @UserPermission
    @PutMapping("update/imageUrl/{imageUrl}")
    public ResponseEntity<?> updateImageUrl(@PathVariable String imageUrl,
                                        @AuthenticationPrincipal User user) {
        return service.updateImageUrl(imageUrl, user);
    }

    @AdminPermission
    @DeleteMapping("delete/{email}")
    public ResponseEntity<?> delete(@PathVariable String email) {
        return service.delete(email);
    }
}

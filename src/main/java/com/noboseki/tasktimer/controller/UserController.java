package com.noboseki.tasktimer.controller;

import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.playload.UserServiceCreateRequest;
import com.noboseki.tasktimer.playload.UserServiceGetResponse;
import com.noboseki.tasktimer.playload.UserServiceUpdateRequest;
import com.noboseki.tasktimer.security.perms.UserPermission;
import com.noboseki.tasktimer.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping("create")
    public ResponseEntity<String> create(@RequestBody UserServiceCreateRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @UserPermission
    @GetMapping("get")
    public ResponseEntity<UserServiceGetResponse> get(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.get(user));
    }

    @UserPermission
    @PutMapping("update")
    public ResponseEntity<String> updateProfile(@AuthenticationPrincipal User user, @RequestBody UserServiceUpdateRequest request) {
        return ResponseEntity.ok(service.updateProfile(user, request));
    }
}

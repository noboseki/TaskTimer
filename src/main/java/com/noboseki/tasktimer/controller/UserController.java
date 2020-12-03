package com.noboseki.tasktimer.controller;

import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.playload.ApiResponse;
import com.noboseki.tasktimer.playload.UserServiceGetResponse;
import com.noboseki.tasktimer.playload.UserServiceUpdateRequest;
import com.noboseki.tasktimer.security.perms.UserPermission;
import com.noboseki.tasktimer.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

//    @PostMapping("create")
//    public ResponseEntity<?> create(@RequestBody UserCreateRequest request) {
//        return service.create(request);
//    }

    @UserPermission
    @GetMapping("get")
    public ResponseEntity<UserServiceGetResponse> get(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.get(user));
    }

    @UserPermission
    @PutMapping("update")
    public ResponseEntity<ApiResponse> updateProfile(@AuthenticationPrincipal User user, @RequestBody UserServiceUpdateRequest request) {
        return ResponseEntity.ok(service.updateProfile(user, request));
    }

//    @AdminPermission
//    @GetMapping("get/{email}")
//    public ResponseEntity<?> getByEmail(@PathVariable String email) {
//        return service.getByEmail(email);
//    }
//
//    @UserPermission
//    @PutMapping("update/name/{name}")
//    public ResponseEntity<?> updateName(@PathVariable String name,
//                                        @AuthenticationPrincipal User user) {
//        return service.updateName(name, user);
//    }
//
//    @UserPermission
//    @PutMapping("update/imageUrl/{imageUrl}")
//    public ResponseEntity<?> updateImageUrl(@PathVariable String imageUrl,
//                                        @AuthenticationPrincipal User user) {
//        return service.updateImageUrl(imageUrl, user);
//    }
//
//    @AdminPermission
//    @DeleteMapping("delete/{email}")
//    public ResponseEntity<?> delete(@PathVariable String email) {
//        return service.delete(email);
//    }
}

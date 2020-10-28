package com.noboseki.tasktimer.controller;

import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.security.perms.UserPermission;
import com.noboseki.tasktimer.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("task")
@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    @UserPermission
    @PostMapping("create/{name}")
    public ResponseEntity<?> create(@AuthenticationPrincipal User user,
                                    @Valid @PathVariable String name) {
        return service.create(user,name);
    }

    @UserPermission
    @GetMapping("get/{name}")
    public ResponseEntity<?> get(@AuthenticationPrincipal User user,
                                 @Valid @PathVariable String name) {
        return service.get(user, name);
    }

    @UserPermission
    @GetMapping("getAll")
    public ResponseEntity<?> get(@AuthenticationPrincipal User user) {
        return service.getAll(user);
    }

    @UserPermission
    @PutMapping("updateName/{oldName}/{newName}")
    public ResponseEntity<?> updateName(@AuthenticationPrincipal User user,
                                        @PathVariable String oldName,
                                        @Valid@PathVariable String newName) {
        return service.updateName(user, oldName, newName);
    }

    @UserPermission
    @PutMapping("updateStatus/{name}")
    public ResponseEntity<?> updateName(@AuthenticationPrincipal User user,
                                        @PathVariable String name) {
        return service.updateIsComplete(user, name);
    }

    @UserPermission
    @DeleteMapping("delete/{name}")
    public ResponseEntity<?> delete(@AuthenticationPrincipal User user,
                                    @PathVariable String name) {
        return service.delete(user, name);
    }
}

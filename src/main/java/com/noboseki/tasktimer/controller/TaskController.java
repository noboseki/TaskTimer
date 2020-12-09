package com.noboseki.tasktimer.controller;

import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.playload.ApiResponse;
import com.noboseki.tasktimer.playload.TaskServiceGetTaskList;
import com.noboseki.tasktimer.security.perms.UserPermission;
import com.noboseki.tasktimer.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    @UserPermission
    @GetMapping("getTasks")
    public ResponseEntity<List<TaskServiceGetTaskList>> getTasks(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.getTasks(user));
    }

    @UserPermission
    @PutMapping("changeTaskComplete")
    public ResponseEntity<ApiResponse> changeTaskComplete(@AuthenticationPrincipal User user,
                                                          @RequestBody String taskName) {
        return ResponseEntity.ok(service.changeTaskComplete(user, taskName));
    }

    @UserPermission
    @PutMapping("changeTaskArchive")
    public ResponseEntity<ApiResponse> changeTaskArchive(@AuthenticationPrincipal User user,
                                                         @RequestBody String taskName) {
        return ResponseEntity.ok(service.changeArchiveTask(user, taskName));
    }

    @UserPermission
    @DeleteMapping("/{taskName}")
    public ResponseEntity<ApiResponse> delete(@AuthenticationPrincipal User user,
                                              @PathVariable String taskName) {
        return ResponseEntity.ok(service.delete(user,taskName));
    }
}

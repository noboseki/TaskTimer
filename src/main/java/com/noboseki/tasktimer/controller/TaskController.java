package com.noboseki.tasktimer.controller;

import com.noboseki.tasktimer.playload.TaskServiceGetTaskList;
import com.noboseki.tasktimer.security.UserDetailsImpl;
import com.noboseki.tasktimer.security.perms.UserPermission;
import com.noboseki.tasktimer.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    @UserPermission
    @PostMapping("")
    public ResponseEntity<String> create(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @RequestBody @Min(5) @Max(25) String taskName) {
        return ResponseEntity.ok(service.create(userDetails, taskName));
    }

    @UserPermission
    @GetMapping("getTasks")
    public ResponseEntity<List<TaskServiceGetTaskList>> getTasks(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(service.getTasks(userDetails));
    }

    @UserPermission
    @PutMapping("changeTaskComplete")
    public ResponseEntity<String> changeTaskComplete(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @RequestBody String taskName) {
        return ResponseEntity.ok(service.changeTaskComplete(userDetails, taskName));
    }

    @UserPermission
    @PutMapping("changeTaskArchive")
    public ResponseEntity<String> changeTaskArchive(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                    @RequestBody String taskName) {
        return ResponseEntity.ok(service.changeArchiveTask(userDetails, taskName));
    }

    @UserPermission
    @DeleteMapping("/{taskName}")
    public ResponseEntity<String> delete(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @PathVariable String taskName) {
        return ResponseEntity.ok(service.delete(userDetails, taskName));
    }
}

/*
package com.noboseki.tasktimer.controller;

import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.security.perms.UserPermission;
import com.noboseki.tasktimer.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("task")
@RestController
public class TaskController {

    private TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @UserPermission
    @PostMapping("create")
    public ResponseEntity<?> create(Task.TaskDto dto) {
        return service.create(dto);
    }

    @UserPermission
    @GetMapping("get/{uuid}")
    public ResponseEntity<?> get(@PathVariable UUID uuid) {
        return service.get(uuid);
    }

    @UserPermission
    @PutMapping("update")
    public ResponseEntity<?> update(Task.TaskDto dto) {
        return service.update(dto);
    }

    @UserPermission
    @DeleteMapping("delete/{uuid}")
    public ResponseEntity<?> delete(@PathVariable UUID uuid) {
        return service.delete(uuid);
    }
}*/

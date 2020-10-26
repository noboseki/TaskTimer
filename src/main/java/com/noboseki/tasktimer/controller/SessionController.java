/*
package com.noboseki.tasktimer.controller;

import com.noboseki.tasktimer.domain.Session;
import com.noboseki.tasktimer.security.perms.UserPermission;
import com.noboseki.tasktimer.service.SessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("workTime")
@RestController
public class SessionController {

    private SessionService service;

    public SessionController(SessionService service) {
        this.service = service;
    }

    @UserPermission
    @PostMapping("create")
    public ResponseEntity<?> create(Session.SessionDto dto) {
        return service.create(dto);
    }

    @UserPermission
    @GetMapping("get/{uuid}")
    public ResponseEntity<?> get(@PathVariable UUID uuid) {
        return service.get(uuid);
    }

    @UserPermission
    @PutMapping("update")
    public ResponseEntity<?> update(Session.SessionDto dto) {
        return service.update(dto);
    }

    @UserPermission
    @DeleteMapping("delete/{uuid}")
    public ResponseEntity<?> delete(@PathVariable UUID uuid) {
        return service.delete(uuid);
    }
}
*/

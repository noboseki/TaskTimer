package com.noboseki.tasktimer.controller;

import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.playload.CreateSessionRequest;
import com.noboseki.tasktimer.playload.SessionServiceTableByDateResponse;
import com.noboseki.tasktimer.playload.SessionServiceChainByDateResponse;
import com.noboseki.tasktimer.security.perms.UserPermission;
import com.noboseki.tasktimer.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("session")
@RestController
@RequiredArgsConstructor
public class SessionController {

    private final SessionService service;

    @UserPermission
    @PostMapping("create/{taskName}")
    public ResponseEntity<?> create(@AuthenticationPrincipal User user,
                                    @PathVariable String taskName,
                                    @RequestBody CreateSessionRequest request) {
        return service.create(user, taskName, request);
    }

    @UserPermission
    @GetMapping("getByTask/{task}")
    public ResponseEntity<?> getByTask(@AuthenticationPrincipal User user,
                                       @PathVariable String task) {
        return service.getAllByTask(user, task);
    }

    @UserPermission
    @GetMapping("getChainByDate/{from}/{to}")
    public ResponseEntity<SessionServiceChainByDateResponse> getChainByDate(@AuthenticationPrincipal User user,
                                                                            @PathVariable String from,
                                                                            @PathVariable String to) {
        return ResponseEntity.ok(service.getBarChainByDate(user, LocalDate.parse(from), LocalDate.parse(to)));
    }

    @UserPermission
    @GetMapping("getTableByDate/{from}/{to}")
    public ResponseEntity<List<SessionServiceTableByDateResponse>> getTableByDate(@AuthenticationPrincipal User user,
                                                                                  @PathVariable String from,
                                                                                  @PathVariable String to) {
        return ResponseEntity.ok(service.getTableByDate(user, LocalDate.parse(from), LocalDate.parse(to)));
    }
}

package com.noboseki.tasktimer.controller;

import com.noboseki.tasktimer.playload.GetByTaskSessionResponse;
import com.noboseki.tasktimer.playload.SessionServiceChainByDateResponse;
import com.noboseki.tasktimer.playload.SessionServiceCreateRequest;
import com.noboseki.tasktimer.playload.SessionServiceTableByDateResponse;
import com.noboseki.tasktimer.security.UserDetailsImpl;
import com.noboseki.tasktimer.security.perms.UserPermission;
import com.noboseki.tasktimer.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("session")
@RestController
@RequiredArgsConstructor
public class SessionController {

    private final SessionService service;

    @UserPermission
    @PostMapping("create")
    public ResponseEntity<String> create(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @RequestBody SessionServiceCreateRequest request) {
        return ResponseEntity.ok(service.create(userDetails, request));
    }

    @UserPermission
    @GetMapping("getByTask/{task}")
    public ResponseEntity<List<GetByTaskSessionResponse>> getByTask(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                    @PathVariable String task) {
        return ResponseEntity.ok(service.getAllByTask(userDetails, task));
    }

    @UserPermission
    @GetMapping("getChainByDate/{from}/{to}")
    public ResponseEntity<SessionServiceChainByDateResponse> getChainByDate(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                            @PathVariable String from,
                                                                            @PathVariable String to) {
        return ResponseEntity.ok(service.getBarChainByDate(userDetails, from, to));
    }

    @UserPermission
    @GetMapping("getTableByDate/{from}/{to}")
    public ResponseEntity<List<SessionServiceTableByDateResponse>> getTableByDate(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                                  @PathVariable String from,
                                                                                  @PathVariable String to) {
        return ResponseEntity.ok(service.getTableByDate(userDetails, from, to));
    }
}

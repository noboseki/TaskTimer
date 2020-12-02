package com.noboseki.tasktimer.controller;

import com.noboseki.tasktimer.domain.ProfileImg;
import com.noboseki.tasktimer.security.perms.UserPermission;
import com.noboseki.tasktimer.service.ProfileImgService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("profileImg")
@RestController
@RequiredArgsConstructor
public class ProfileImgController {

    private final ProfileImgService service;

    @UserPermission
    @GetMapping("getAll")
    public ResponseEntity<List<ProfileImg>> getAllIcons() {
        return ResponseEntity.ok(service.getAllIcons());
    }
}

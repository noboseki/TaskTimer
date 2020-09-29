package com.noboseki.tasktimer.controller;

import com.noboseki.tasktimer.domain.WorkTime;
import com.noboseki.tasktimer.service.WorkTimeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("workTime")
@RestController
public class WorkTimeController {

    private WorkTimeService service;

    public WorkTimeController(WorkTimeService service) {
        this.service = service;
    }

    @PostMapping("create")
    public ResponseEntity<?> create(WorkTime.WorkTimeDto dto) {
        return service.create(dto);
    }

    @GetMapping("get/{uuid}")
    public ResponseEntity<?> get(@PathVariable UUID uuid) {
        return service.get(uuid);
    }

    @PutMapping("update")
    public ResponseEntity<?> update(WorkTime.WorkTimeDto dto) {
        return service.update(dto);
    }

    @DeleteMapping("delete/{uuid}")
    public ResponseEntity<?> delete(@PathVariable UUID uuid) {
        return service.delete(uuid);
    }
}
